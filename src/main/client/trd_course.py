#!/usr/bin/python
# Beacon Laptiming Adaptation (Course version)
# PONCHEL JEREMY - 18/05/2016

import blescan
import sys
import time
import string
import mysql.connector as mariadb

import bluetooth._bluetooth as bluez

dev_id = 0
try:
	sock = bluez.hci_open_dev(dev_id)
	print "Loading Scanner ..."

except:
	print "Not bluetooth device found ..."
    	sys.exit(1)

blescan.hci_le_set_scan_parameters(sock)
blescan.hci_enable_le_scan(sock)

wait_time=29
valid_lap=0

conn = mariadb.connect(host="localhost",user="timingsystem",password="7i7ev84n", database="timingsystem")
cursor = conn.cursor(buffered=True)

while True:
	current = blescan.parse_events(sock,1)
	if len(current) <> 0:
		data = string.split(current[0],",")
		try :
			major = int(data[2])
			minor = int(data[3])
		except :
			major = ""
			minor = ""
		if isinstance(major,int) & isinstance(minor,int):
			now = time.time()
			cursor.execute("SELECT timenow,major FROM inventory_course WHERE major=%s ORDER BY id DESC", (data[2],))
            		if cursor.rowcount <> 0:
                            	lastcurrent = cursor.fetchmany(size=1)
                            	current_time = float(now) - float(lastcurrent[0][0])
				if float(current_time)>float(wait_time):
                	                valid_lap=2
				else:
                                    	valid_lap=0
            		else:
                        	print "First Lap"
                            	valid_lap=1
			if valid_lap == 1:
				try:
    					current = (now,data[1],data[2],data[3],data[5])
	    				cursor.execute("INSERT INTO `inventory_course`(`timenow`, `uuid`, `major`, `minor`, `power`) VALUES (%s,%s,%s,%s,%s)", current)
				except mariadb.Error as error:
    					print("Error: {}".format(error))
				conn.commit()
				print "First Lap Beacon : ", data[2]
			if valid_lap == 2:
				try:
					current = (current_time,data[2])
					cursor.execute("INSERT INTO `lap_course`(`timelap`, `beacon`) VALUES (%s,%s)",current)
    					current = (now,data[5],data[2])
					cursor.execute("UPDATE `inventory_course` SET `timenow`=%s,`power`=%s WHERE `major`=%s", current)
				except mariadb.Error as error:
    					print("Error: {}".format(error))
				conn.commit()
				print "Beacon : ", data[2]
