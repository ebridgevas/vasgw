%AXIS2_HOME%\bin\wsdl2java -uri http://10.10.4.28:8080/RegisterSubscriber/RegisterSubscriber?wsdl -p com.ebridgecommerce.sdp.disruptor.webserviceclient.simregistration -d xmlbeans -s -o build\client


nohup /prod/vasgw/bin/smpptr6.sh 196.2.77.23 2775 SimReg pwd132 SMS SimReg pwd132 >> /var/log/ebridge/SimReg.log 2>&1 < /dev/null &
nohup /prod/vasgw/bin/smpptr6.sh 196.2.77.25 2775 SimReg pwd132 SMS SimReg pwd132 >> /var/log/ebridge/SimReg.log 2>&1 < /dev/null &