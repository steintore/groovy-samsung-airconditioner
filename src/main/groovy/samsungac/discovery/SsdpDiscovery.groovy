package samsungac.discovery

class SsdpDiscovery {

    static final int PORT = 1900;
    static final String NEWLINE = '\r\n';

    static final Map discoverParams = [
            NOTIFY         : "NOTIFY * HTTP/1.1",
            HOST           : "239.255.255.250:$PORT",
            'CACHE-CONTROL': 'max-age=20',
            SERVER         : 'AIR CONDITIONER',
            'SPEC_VER'     : 'MSpec-1.00',
            'SERVICE_NAME' : 'ControlServer-MLib',
            'MESSAGE_TYPE' : 'CONTROLLER_START'
    ]

    def static discover() {
        sendNotify(constructNotifyMessage())
        def response = parseResponse(retrieveResponse())
        println "Got the following response from Samsung Air Conditioner: $response"
        response
    }

    private static def parseResponse(String response) {
        Map device = [:]
        response.split(NEWLINE).findAll {it.contains(': ')}.each {
            def splitted = it.split(': ')
            device.put(splitted.first(), splitted.last())
        }
        device.put('IP', device.LOCATION?.split('//')?.last()?.toString())
        device
    }

    static def retrieveResponse() {
        def response = null
        MulticastSocket recSocket = setUpSocket()

        int i = 0
        while (!response) {
            byte[] buf = new byte[2048]
            DatagramPacket input = new DatagramPacket(buf, buf.length)
            try {
                recSocket.receive(input)
                response = new String(input.data)
            } catch (SocketTimeoutException e) {
                // TODO fix handling of time out
                if (i >= 2) break
                i++
            }
        }
        if (!response) throw new Exception('No air conditioner found')
        response
    }

    private static MulticastSocket setUpSocket() {
        MulticastSocket recSocket = new MulticastSocket(null)
        recSocket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), PORT))
        recSocket.setTimeToLive(10)
        recSocket.setSoTimeout(1000)
        recSocket.broadcast = true
        recSocket
    }

    private static def sendNotify(def notifyMessage) {
        MulticastSocket socket = new MulticastSocket(null)
        try {
            socket.bind(new InetSocketAddress(InetAddress.localHost.canonicalHostName, PORT))
            byte[] data = notifyMessage.toString().bytes
            socket.send(new DatagramPacket(data, data.length, new InetSocketAddress(getBroadCastAddress(), PORT)))
        } catch (IOException e) {
            throw e
        } finally {
            socket.disconnect()
            socket.close()
        }
    }

    private static def constructNotifyMessage() {
        StringBuilder packet = new StringBuilder()
        discoverParams.each { key, value ->
            packet.append("$key: $value").append(NEWLINE)
        }
        packet
    }

    private static def getBroadCastAddress() {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement()
            if (networkInterface.isLoopback())
                continue;    // Don't want to broadcast to the loopback interface
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast()
                if (broadcast) return broadcast
            }
        }
    }

}
