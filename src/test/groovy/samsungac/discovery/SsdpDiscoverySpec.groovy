package samsungac.discovery

import spock.lang.Specification

class SsdpDiscoverySpec extends Specification {

    def setup() {
        SsdpDiscovery.metaClass.static.sendNotify = {}
        SsdpDiscovery.metaClass.static.retrieveResponse = { return response }
    }

    def "should return map with info"() {
        when:
        def result = SsdpDiscovery.discover()

        then:
        result.size() == 12
        result."MAC_ADDR" == '7825AD103D06'
        result.LOCATION == 'http://192.168.1.4'
        result.IP == '192.168.1.4'
    }

    def response =
            "NOTIFY * HTTP/1.1" + SsdpDiscovery.NEWLINE +
                    "LOCATION: http://192.168.1.4" + SsdpDiscovery.NEWLINE +
                    "NTS: ssdp:alive" + SsdpDiscovery.NEWLINE +
                    "CACHE_CONTROL: max-age=60" + SsdpDiscovery.NEWLINE +
                    "HOST: 255.255.255.255:1900" + SsdpDiscovery.NEWLINE +
                    "SERVER: SSDP,SAMSUNG-AC-BORACAY" + SsdpDiscovery.NEWLINE +
                    "MAC_ADDR: 7825AD103D06" + SsdpDiscovery.NEWLINE +
                    "SERVICE_NAME: ControlServer-MLib" + SsdpDiscovery.NEWLINE +
                    "SPEC_VER: MSpec-1.00" + SsdpDiscovery.NEWLINE +
                    "MESSAGE_TYPE: DEVICEDESCRIPTION" + SsdpDiscovery.NEWLINE +
                    "NICKNAME: 536D61727420412F432837383235414431303344303629" + SsdpDiscovery.NEWLINE +
                    "MODELCODE: SAMSUNG_DEVICE" + SsdpDiscovery.NEWLINE
}
