

3 alag-alag terminal/command prompt windows:

Terminal 1: docker-compose up chal raha hai (Kafka, Zookeeper, MirrorMaker).

Terminal 2: producer-service chal raha hai (mvn spring-boot:run se).

Terminal 3: consumer-service chal raha hai (mvn spring-boot:run se).

Step 1: Verify Karein ki Sab Kuch Sahi Se Start Hua Hai

Sabse pehle, hum check karenge ki hamara environment (Docker aur applications) bina kisi error ke chal raha hai ya nahi.

Action 1.1: Docker Containers Check Karein

Ek naya (chautha) terminal kholen aur yeh command chalayein:

Generated bash
docker ps

Expected Output 1.1:

Aapko 5 container UP status mein dikhne chahiye. Naam thode alag ho sakte hain, lekin yeh sab honge:

Generated code
CONTAINER ID   IMAGE                           COMMAND                  CREATED          STATUS          PORTS                                           NAMES
...            confluentinc/cp-kafka:7.3.0     "/etc/confluent/dock…"   ...              Up ...          0.0.0.0:9093->9093/tcp, ...                     kafka-remote
...            confluentinc/cp-kafka:7.3.0     "/etc/confluent/dock…"   ...              Up ...                                                          mirrormaker
...            confluentinc/cp-kafka:7.3.0     "/etc/confluent/dock…"   ...              Up ...          0.0.0.0:9092->9092/tcp, ...                     kafka-local
...            confluentinc/cp-zookeeper:7.3.0 "/etc/confluent/dock…"   ...              Up ...          0.0.0.0:2182->2181/tcp, ...                     zookeeper-remote
...            confluentinc/cp-zookeeper:7.3.0 "/etc/confluent/dock…"   ...              Up ...          ... , 0.0.0.0:2181->2181/tcp                      zookeeper-local


Agar yeh output aa raha hai, to aapka Kafka environment bilkul sahi chal raha hai.

Action 1.2: Producer Service ka Log Check Karein

Apne Terminal 2 (jahan producer chal raha hai) par jayein.

Expected Output 1.2:

Aapko aakhri lines mein kuch aisa dikhega:

Generated code
...
... o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
... c.e.p.ProducerServiceApplication         : Started ProducerServiceApplication in ... seconds


Agar "Started ProducerServiceApplication" dikh raha hai, to producer sahi se chal raha hai.

Action 1.3: Consumer Service ka Log Check Karein

Apne Terminal 3 (jahan consumer chal raha hai) par jayein.

Expected Output 1.3:

Aapko aakhri lines mein kuch aisa dikhega:

Generated code
...
... o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
... c.e.c.ConsumerServiceApplication         : Started ConsumerServiceApplication in ... seconds


Agar "Started ConsumerServiceApplication" dikh raha hai, to consumer bhi sahi se chal raha hai.

Ab hum actual functionality test karne ke liye taiyar hain!

Step 2: Test Case 1 - Successful Order Replication (The Happy Path)

Is test mein hum ek order bhejenge aur dekhenge ki kya woh local cluster se remote cluster tak pahunchta hai aur consumer usse validate karta hai ya nahi.

Action 2.1: Ek Order Bhejein (Postman ya curl se)

Ek naye terminal mein (ya wahi jahan aapne docker ps chalaya tha), yeh curl command copy-paste karke chalayein:

Generated bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{
    "productId": "BOOK-TS-101",
    "quantity": 1,
    "price": 499.00,
    "customerId": "CUST-XYZ"
}'

Expected Output 2.1 (Aapke curl terminal mein):

Command chalate hi aapko turant yeh response milna chahiye:

Generated code
Order sent to local cluster successfully! Order ID: a1b2c3d4-e5f6-7890-gh12-i3j4k5l6m7n8


(Note: Order ID har baar alag hoga). Yeh response batata hai ki producer-service ne aapki request le li hai.

Expected Output 2.2 (Producer Service ke Terminal mein):

Turant apne Terminal 2 (producer) ko dekhein. Aapko ek log message dikhega jo batata hai ki order Kafka ko bhej diya gaya hai:

Generated code
INFO ... --- [nio-8080-exec-1] c.e.p.s.OrderProducerService           : Sending Order -> Order{orderId='...', productId='BOOK-TS-101', quantity=1, price=499.0, customerId='CUST-XYZ', orderTimestamp=...} to topic -> orders

Expected Output 2.3 (Consumer Service ke Terminal mein):

Ab sabse important. Apne Terminal 3 (consumer) ko dekhein. 1-2 second ke andar, MirrorMaker data ko replicate karega aur consumer usse padh lega. Aapko do log messages dikhenge:

Pehla message ki order receive ho gaya:

Generated code
INFO ... --- [ntainer#0-0-C-1] c.e.c.s.OrderValidatorConsumer         : <<<<< Received replicated order: Order{orderId='...', productId='BOOK-TS-101', quantity=1, price=499.0, customerId='CUST-XYZ', orderTimestamp=...}


Doosra message ki order validation safal raha:

Generated code
INFO ... --- [ntainer#0-0-C-1] c.e.c.s.OrderValidatorConsumer         : >>>>> Order VALIDATED SUCCESSFULLY: ...


Result: Agar aapko yeh teeno jagah (curl, producer log, consumer log) expected output mil gaya hai, to aapka Project 100% sahi kaam kar raha hai! Mubarak ho!

Step 3: Test Case 2 - Replication Failure Test (Resilience)

Ab hum project description ki ek specific requirement test karenge: "Resilient fallbacks in case replication breaks". Hum MirrorMaker ko band karke dekhenge ki kya hamara data safe rehta hai.

Action 3.1: MirrorMaker ko Rokein

Terminal mein MultiRegionOrderReplicator folder ke andar se yeh command chalayein:

Generated bash
docker-compose stop mirrormaker

Expected Output 3.1:

Terminal mein Stopping mirrormaker ... done likha aayega. Iska matlab ab local aur remote cluster ke beech replication ruk gaya hai.

Action 3.2: Ek Naya Order Bhejein (Jab Replication Band Hai)

Wapas apna curl command chalayein, is baar alag product ke saath:

Generated bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{
    "productId": "GADGET-EL-202",
    "quantity": 5,
    "price": 1250.00,
    "customerId": "CUST-PQR"
}'

Expected Output 3.2 (Alag-alag terminals mein):

curl Terminal: Aapko wahi success message milega: Order sent to local cluster successfully! ...

Producer Terminal: Aapko phir se Sending Order -> ... ka log dikhega.

Consumer Terminal: YAHAN KUCH NAHI HOGA. Koi naya log message nahi aayega, kyunki replication band hai. Data abhi kafka-local mein ruka hua hai.

Action 3.3: MirrorMaker ko Phir se Start Karein

Ab replication ko wapas chalu karte hain:

Generated bash
docker-compose start mirrormaker

Expected Output 3.3:

Terminal mein Starting mirrormaker ... done aayega.

Expected Output 3.4 (Consumer Service ke Terminal mein):

Ab Terminal 3 (consumer) ko dhyan se dekhein. Jaise hi MirrorMaker start hoga, woh kafka-local mein pending message (jo humne step 3.2 mein bheja tha) uthayega aur kafka-remote par bhej dega. Aapko turant woh message consumer ke log mein dikh jayega:

Generated code
INFO ... --- [ntainer#0-0-C-1] c.e.c.s.OrderValidatorConsumer         : <<<<< Received replicated order: Order{orderId='...', productId='GADGET-EL-202', ...}
INFO ... --- [ntainer#0-0-C-1] c.e.c.s.OrderValidatorConsumer         : >>>>> Order VALIDATED SUCCESSFULLY: ...


Result: Agar yeh ho gaya, to aapne safaltapoorvak sabit kar diya ki aapka system resilient hai aur replication rukne par data loss nahi hota.

Step 4: Sab Kuch Band Kaise Karein (Cleanup)

Testing poori hone ke baad, in steps se sab kuch saaf-suthre tareeke se band karein.

Producer aur Consumer applications ko band karein:

Terminal 2 (producer) mein Ctrl + C dabayein.

Terminal 3 (consumer) mein Ctrl + C dabayein.

Saare Docker containers ko band karein aur remove karein:

Terminal mein (jahan docker-compose.yml file hai) yeh command chalayein:

Generated bash
docker-compose down