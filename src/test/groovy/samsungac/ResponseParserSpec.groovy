package samsungac

import spock.lang.Specification


class ResponseParserSpec extends Specification {

    def "reading token should save it"() {
        given:
        def response = '<?xml version="1.0" encoding="utf-8" ?><Update Type="GetToken" Status="Completed" Token="33965903-4482-M849-N716-373832354144"/>'

        when:
        assert ResponseParser.isResponseWithToken(response)

        and:
        def token = ResponseParser.parseTokenFromResponse(response)

        then:
        token == '33965903-4482-M849-N716-373832354144'
    }

    def "is failed response should return true if failed"() {
        given:
        def response = '<?xml version="1.0" encoding="utf-8" ?><Response Status="Fail" Type="Authenticate" ErrorCode="301" />'

        expect:
        ResponseParser.isFailedAuthenticationResponse(response)

    }

    def "is failed response should return false if not failed"() {
        given:
        def response = '<?xml version="1.0" encoding="utf-8" ?><Response Status="Failed" Type="Authenticate" ErrorCode="301" />\''

        expect:
        !ResponseParser.isFailedAuthenticationResponse(response)
    }

    def "is not logged in successful"() {
        given:
        def response = ''

        expect:
        !ResponseParser.isSuccessfulLoginResponse(response)
    }

    def "is logged in successful"() {
        given:
        def response = '<?xml version="1.0" encoding="utf-8" ?><Response Type="AuthToken" Status="Okay" StartFrom="2014-10-11/15:43:40"/>'

        expect:
        ResponseParser.isSuccessfulLoginResponse(response)
    }

    def "should parse status response to map"() {
        when:
        def status = ResponseParser.parseStatusResponse(statusResponse)

        then:
        status.AC_FUN_POWER == 'On'
        status.AC_FUN_TEMPNOW == 21
        status.AC_FUN_OPMODE == 'Heat'
        status.AC_FUN_ENABLE == 'Enable'
        status.AC_FUN_SUPPORTED == 0
        status.AC_FUN_TEMPSET == 20
        status.AC_FUN_COMODE == 'Off'
        status.AC_FUN_ERROR == 0
        status.AC_FUN_TEMPNOW == 21
        status.AC_FUN_SLEEP == 0
        status.AC_FUN_WINDLEVEL == 'Auto'
        status.AC_FUN_DIRECTION == 'Fixed'
        status.AC_ADD_AUTOCLEAN == 'Off'
        status.AC_ADD_APMODE_END == 0
        status.AC_ADD_STARTWPS == 'Direct'
        status.AC_ADD_SPI == 'Off'
        status.AC_SG_WIFI == 'Connected'
        status.AC_SG_INTERNET == 'Connected'
        status.AC_ADD2_VERSION == 0
        status.AC_SG_MACHIGH == 0
        status.AC_SG_MACMID == 0
        status.AC_SG_MACLOW == 0
        status.AC_SG_VENDER01 == 0
        status.AC_SG_VENDER02 == 0
        status.AC_SG_VENDER03 == 0
    }

    def "should return true if response has correct command id"() {
        given:
        def line = '<Request Type="DeviceControl"><Control CommandID="cmd8005" DUID="7825AD1243BA"><Attr ID="AC_FUN_TEMPSET" Value="21" /></Control></Request>'

        expect:
        assert ResponseParser.isCorrectCommandResponse(line, "cmd8005")

        and:
        assert !ResponseParser.isCorrectCommandResponse(line, "cmd8006")
    }

    def "should return true if DeviceControl"() {
        given:
        def line = '<?xml version="1.0" encoding="utf-8" ?><Response Type="DeviceControl" Status="Okay" DUID="7825AD1243BA" CommandID="cmd8005"/>'

        expect:
        assert ResponseParser.isDeviceControl(line)
    }

    def "should return true if response is DeviceState"() {
        expect:
        assert ResponseParser.isDeviceState(statusResponse)
    }

    def "get status value should return the value"() {
        given:
        def line = '<?xml version="1.0" encoding="utf-8" ?><Response Type="DeviceControl" Status="Okay" DUID="7825AD1243BA" CommandID="cmd8005"/>'

        expect:
        ResponseParser.getStatusValue(line) == 'Okay'
    }


//<?xml version="1.0" encoding="utf-8" ?>
    def statusResponse = """
<Response Type="DeviceState" Status="Okay">
<DeviceState>
    <Device DUID="7825AD1243BA" GroupID="AC" ModelID="AC" >
        <Attr ID="AC_FUN_ENABLE" Type="RW" Value="Enable"/>
        <Attr ID="AC_FUN_POWER" Type="RW" Value="On"/>
        <Attr ID="AC_FUN_SUPPORTED" Type="R" Value="0"/>
        <Attr ID="AC_FUN_OPMODE" Type="RW" Value="Heat"/>
        <Attr ID="AC_FUN_TEMPSET" Type="RW" Value="20"/>
        <Attr ID="AC_FUN_COMODE" Type="RW" Value="Off"/>
        <Attr ID="AC_FUN_ERROR" Type="RW" Value="00000000"/>
        <Attr ID="AC_FUN_TEMPNOW" Type="R" Value="21"/>
        <Attr ID="AC_FUN_SLEEP" Type="RW" Value="0"/>
        <Attr ID="AC_FUN_WINDLEVEL" Type="RW" Value="Auto"/>
        <Attr ID="AC_FUN_DIRECTION" Type="RW" Value="Fixed"/>
        <Attr ID="AC_ADD_AUTOCLEAN" Type="RW" Value="Off"/>
        <Attr ID="AC_ADD_APMODE_END" Type="W" Value="0"/>
        <Attr ID="AC_ADD_STARTWPS" Type="RW" Value="Direct"/>
        <Attr ID="AC_ADD_SPI" Type="RW" Value="Off"/>
        <Attr ID="AC_SG_WIFI" Type="W" Value="Connected"/>
        <Attr ID="AC_SG_INTERNET" Type="W" Value="Connected"/>
        <Attr ID="AC_ADD2_VERSION" Type="RW" Value="0"/>
        <Attr ID="AC_SG_MACHIGH" Type="W" Value="0"/>
        <Attr ID="AC_SG_MACMID" Type="W" Value="0"/>
        <Attr ID="AC_SG_MACLOW" Type="W" Value="0"/>
        <Attr ID="AC_SG_VENDER01" Type="W" Value="0"/>
        <Attr ID="AC_SG_VENDER02" Type="W" Value="0"/>
        <Attr ID="AC_SG_VENDER03" Type="W" Value="0"/>
    </Device>
</DeviceState>
</Response>
"""
}
