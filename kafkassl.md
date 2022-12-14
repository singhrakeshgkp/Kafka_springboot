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
  ### SetupLocal Certificate Authority
   - Download openssl. I have downloaded form this link[link](https://code.google.com/archive/p/openssl-for-windows/downloads)
   - Extract and set bin folder path in environment variable
   - create new env variable <b>OPENSSL_CONF</b> set complete(including file name) openssl.cnf file path
   - Open command prompt and run below command
   ```  openssl req -new -x509 -keyout <file name example ca-key> -out ca-cert -days <days 180, 360 etc.. > -subj "/CN=local-security-CA"  ```
   - Enter any password
   - Press enter button, one additional ca file with the given name will be generted, this is the private key that u should never ever share to anyone 
  ### Create CSR(certificate signing request)
  ### Sign the SSL certificate
  ### Add the signed ssl certificate to keystore.jks file
  ### Configure the SSL certificate in kafka broker
  ### create truststore.jks for client
  
  - df
  - sdfsd
  - dsfdsf
  - fsdf
  - sfds
  - fds
  - fds
  - f

- Steps to setup SSL
    \
    - 
    - Sign the SSL certificate
    - Add the signed ssl certificate to keystore.jks file
    - Configure the SSL certificate in kafka broker
    - create truststore.jks for client 
