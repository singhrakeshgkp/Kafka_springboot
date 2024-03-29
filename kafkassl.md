# Steps to setup SSL

  ### Generate keystore.jks
   - use the below command to generate keystore.jsk file
   ``` 
   keytool -keystore <keystore name>.keystore.jks -alias <alias example localhost> -validity <validity in days example 180,360..> -genkey -keyalg RSA
   ```
   - Use below command to view the keystore.jks file
   ```
   keytool -list -v -keystore <keystore file name ex. testserver.keystore.jks>
   ```
  ### Steps to SetupLocal Certificate Authority and Create CSR(Certificateh singning request)
   - <b>Setup Certificate Authority </b> 
       - Download openssl. I have downloaded form this [link](https://code.google.com/archive/p/openssl-for-windows/downloads)
       - Extract and set bin folder path in environment variable
       - create new env variable <b>OPENSSL_CONF</b> set complete(including file name) openssl.cnf file path
       - Open command prompt and run below command
       ```  openssl req -new -x509 -keyout <file name example ca-key> -out ca-cert -days <days 180, 360 etc.. > -subj "/CN=local-security-CA"  ```
       - Enter any password
       - Press enter button, one additional ca file with the given name will be generted, this is the private key that u should never ever share to anyone 
   -  <b>Create CSR </b>
      - Use the below command to Create CSR
      ```
      keytool -keystore <generated key store file name testserver.keystore.jks -alias localhost -certreq -file <cert file name ex. test-cert>
      
      ```
      - One additional file will be generated with test-cert name
    
  ### Sign the SSL certificate
   - use below command to sign the test-cert file
   ```
   openssl x509 -req -CA ca-cert -CAkey ca-key -in <cert file name test-cert> -out <output file name test-cert-signed> -days <180> -CAcreateserial -passin pass:<pwd    provided while generating file>
   ```
   - one more file wit the given file name will be generated, in my case it is test-cert-signed
   - To see the content in the generated signed file use command    ``` keytool -printcert -v -file <signed file name> ```
  ### Add the signed ssl certificate to keystore.jks file
   - Use below two command to add ca-cert and signed cert to generated keystore.jks file
   ```
   keytool -keystore testserver.keystore.jks -alias CARoot -import -file ca-cert
   keytool -keystore testserver.keystore.jks -alias localhost -import -file test-cert-signed
   ```
   -   
  ### Configure the SSL certificate in kafka broker
   - Configure Below properties in kafka server.properties file
   ```
   listeners=PLAINTEXT://:9092, ssl://localhost:9192
   ssl.keystore.location=<jks file path>/server.keystore.jks
   ssl.keystore.password=pwd
   ssl.key.password=pwd
   ssl.endpoint.identification.algorithm=
   ```
  ### create client truststore.jks for client
  
  - Use below command to crete truststore.jks file for client.
  ```
  keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
  ```
  
