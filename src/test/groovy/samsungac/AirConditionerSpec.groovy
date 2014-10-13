package samsungac

import groovy.mock.interceptor.MockFor
import spock.lang.Specification

import javax.net.ssl.HandshakeCompletedListener
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket

class AirConditionerSpec extends Specification {

    def setup() {
        def mock = new MockFor(SSLSocket.class)
        mock.use {
            AirConditioner.metaClass.connect = { ->
                return mock
            }
        }
    }

    def "Connect and switch temp to 25"() {
        given:
        def aircon = new AirConditioner([LOCATION: 'http://192.168.1.140', MAC_ADDR: '7825AD1243BA'], '33965903-4482-M849-N716-373832354144')

        when:
        aircon.login()

        aircon.on()
        aircon.setTemperature(21)

        then:
        aircon.getTemperatureNow() == 21
    }

    def "should connect to air conditioner and get token"() {
        given:
        def aircon = new AirConditioner([LOCATION: 'http://192.168.1.140', MAC_ADDR: '7A3E4R567T54'], null)

        when:
        def token = aircon.getToken()

        then:
        noExceptionThrown()
        token
    }

    def "should connect when having a token"() {
        given:
        def aircon = new AirConditioner([LOCATION: 'http://192.168.1.140', MAC_ADDR: '7825AD1243BA'], '33965903-4482-M849-N716-373832354144')

        when:
        def result = aircon.getToken()

        then:
        result
    }

    class MockSocket extends SSLSocket {

        @Override
        String[] getSupportedCipherSuites() {
            return new String[0]
        }

        @Override
        String[] getEnabledCipherSuites() {
            return new String[0]
        }

        @Override
        void setEnabledCipherSuites(String[] strings) {

        }

        @Override
        String[] getSupportedProtocols() {
            return new String[0]
        }

        @Override
        String[] getEnabledProtocols() {
            return new String[0]
        }

        @Override
        void setEnabledProtocols(String[] strings) {

        }

        @Override
        SSLSession getSession() {
            return null
        }

        @Override
        void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        void startHandshake() throws IOException {

        }

        @Override
        void setUseClientMode(boolean b) {

        }

        @Override
        boolean getUseClientMode() {
            return false
        }

        @Override
        void setNeedClientAuth(boolean b) {

        }

        @Override
        boolean getNeedClientAuth() {
            return false
        }

        @Override
        void setWantClientAuth(boolean b) {

        }

        @Override
        boolean getWantClientAuth() {
            return false
        }

        @Override
        void setEnableSessionCreation(boolean b) {

        }

        @Override
        boolean getEnableSessionCreation() {
            return false
        }
    }
}
