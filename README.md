# camp.test 

## Goals
A simple test case to exemplify the use of the CAMP framework via the REST service points.  

In order for the test-case to execute it is best to download and start the <a href="http://www.camsolute.com/camp.demo.tar.gz">camp.demo</a> package.
(see the Readme.md file on setting up and running the camp.demo package)

In its current form the test-case consists of: 
** the OrderProcessAPITest.testProcessWalk() test-case 
The "testProcessWalk" walks through the Process executing the following actions:
- Start Process 
- Submit Order (completes the "customer order process" user task "submit order")
- Check Order (complete the "customer order management process" user task "check order" -> release to production)
- Ship Order (completes the "production process" user task "complete order")
- Fulfill Order (completes the order "customer order process" user task "confirm fulfillment")
    
The camp.test roadmap will eventually be extended with currently existing test-cases and future example test-cases.

## NOTICE

This is work in progress ...  
 
