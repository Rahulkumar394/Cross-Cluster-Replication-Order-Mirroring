Cross-Cluster Replication — Order Mirroring
Project Name: MultiRegionOrderReplicator
Goal:
Replicate orders.local to orders.remote using MirrorMaker or Kafka Connect.

Tech Stack:
Java 17
Spring Boot (for producer/consumer)
Kafka MirrorMaker
Docker Compose with 2 Kafka clusters

How It Works:
Service A → orders.local
Replicator → orders.remote
Consumer on remote cluster validates order data
Rules to Follow:
Kafka-Specific:

Use same topic name across clusters
Offset alignment enabled
Monitor replication lag
Code Quality:

Validate data before consumption
Resilient fallbacks in case replication breaks
Testing:

Integration: Check remote consumer consistency
Failure: Replicator down → local buffer test
