groovy-samsung-airconditioner
=============================
[![Build Status](https://travis-ci.org/steintore/groovy-samsung-airconditioner.svg?branch=master)](https://travis-ci.org/steintore/groovy-samsung-airconditioner)

Groovy library to access and control a Samsung Air Conditioner in your home network

# Usage

## Discovery of a device
``` groovy
import samsungac.discovery

SsdpDiscovery.discover()
```
You will then recieve a Map with all the details about the device discovered. This map should be sent to the AirConditioner when starting to communicate with it. As described below

## Connecting and logging in to a device
``` groovy
import samsungac

  def connectionMap = SsdpDiscovery.discover()
  def aircon = new AirConditioner(connectionMap.IP, connectionMap.MAC, '33965903-4482-M849-N716-373832354144')
  aircon.login()
```
Here we connect with the IP, MAC-address and a token. When connecting for the first time, we do not have a
token, so we will only send the IP, and the MAC-address

## Creating a Jar
- Clone the project
- run "gradle jar"
- the jar wil be placed in the "build/libs"-folder
