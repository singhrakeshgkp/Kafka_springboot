# Createing Producer using spring boot
  - Create New Spring boot application with below dependency
     - spring web
     - spring kafka
     - lombok
### Approach -1 Producing messages on default topic 
### Approach -2 Producing messages on default topic synchronously(default behaviour is asynchronous)
### Approach -3 producing messages on the specified topic using producer record.
 - here we passed header and partition as null
### Approach -4 producing messages on the specified topic with header and producer record.
  - Consumer do not provide options to read and print the header passed in
  - For this we have to build consumer application, using this we can read and print the header.
  
<details><summary>Common Kafka Producer Configuration</summary>
<p>
  <b>The property can be found on google (kafka producer config) </b>
- Acks
   - acks = 1 -> Gaurantees that messages is written to a leader, this is default configuration.
   - acks = all -> Gaurantees that messages is written to a leader and all replicas
   - acks=0 -> Not recommended, no gaurantee.
- Retries
   - Integer value ranges form 0-21474883647
   - In spring kafka default value is 21474883647
- retry.backoff.ms
  - Integer value represented in miliscond
  - Default value is 100 ms
  
</p>
</details>

# Testing
<details><summary>Automated Test</summary>
<p>
  
- Automated test Runs against your codebase
- It runs as part of your build
- Easy to capture bug and its requirment for todays software development
- Here we will be using JUNIT tool for automated test.
- Types of automated tests
   - Unit test
   - Integration Test
   - End to End Test
  
</p>
</details>

<details><summary>Integration test using junit 5</summary>
<p>
<b>What is integration tests?</b>
  
- Combines the different layers of the code and verify the behaviour is working as expected for example in our example we have controller, kafka producer and grocery event component to test. This is the flow that we will be testing as part of integration test.
- Integration test setup
   - Create controller and producer package under test directory
   - Create new GroceryControllerIntegrationTest class and annotate with ```@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) ```              annotation. Random_Port attribute is going to generate some random port everytime we launch our application.
     If we do not provide port configuration and we launch our application it will be launched on default port 8080.
  - Create a test method and write the necessary codes.
  
- Easy to capture bug and its requirment for todays software development
- Here we will be using JUNIT tool for automated test.
- Types of automated tests
   - Unit test
   - Integration Test
   - End to End Test
  
</p>
</details>



<details><summary>Unit Test</summary>
<p>
<b>What is unit test?</b>
  
- Focuses on a single unit(method) and mock all other behaviour for example if we want to test controller method, we write unit test for that and other layer will     be mocked.
- Mocks the external dependencies.
  
<b>why unit test?</b>  
  
- Faster compared to Integration test
- It can cover the scenario what not possible with Integration test.
- Mocks the external dependencies.
  
  
</p>
</details>
  


