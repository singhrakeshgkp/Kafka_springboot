# Createing Producer using spring boot
  - Create New Spring boot application with below dependency
     - spring web
     - spring kafka
     - lombok
### Approach -1 Producing messages on default topic 
### Approach -2 Producing messages on default topic synchronously(default behaviour is asynchronous)
### Approach -3 producing messages on the specified topic using producer record.
 - here we passed header and partition as null
### Approach -4 producing messages on the specified topic with header producer record.
  - Consumer do not provide options to read and print the header passed in
  - For this we have to build consumer application, using this we can read and print the header.
  


