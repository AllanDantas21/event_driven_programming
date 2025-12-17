# RabbitMQ - Quick Concepts Guide

## What is RabbitMQ?

**RabbitMQ** is a **Message Broker** that uses the **AMQP** protocol (Advanced Message Queue Protocol) for asynchronous communication between systems, especially in microservices architectures.

### Key features:
- **Asynchronous** communication between services
- **Scalable** and **robust** system
- Widely adopted **AMQP** protocol

## What is it for?

RabbitMQ is ideal when you need **asynchronous** communication between services, where an immediate response is not required.

**Practical example:** On Instagram, when posting a photo, you don't need to wait for all processing.
 The request is sent, receives a 200 status, and processing continues asynchronously in the background.

### Use cases:
- Image/video processing
- Email sending
- Notifications
- Microservices integration
- Long-running tasks

## How does it work?
### Main components:

1. **Producer**: Service that sends messages
2. **Exchange**: Routes messages to queues based on routing rules
3. **Queue**: Stores messages
4. **Consumer**: Service that receives and processes messages

### Basic flow:
```
Producer → Exchange → Queue → Consumer(s)
```

## Exchange

**Exchange** is the component responsible for receiving messages from producers and routing them to the correct queues based on routing rules and bindings.

### Exchange Types:

#### 1. Direct Exchange
- Routes messages to queues based on an **exact match** of the routing key
- Use case: Point-to-point messaging with specific routing

#### 2. Topic Exchange
- Routes messages to queues based on **pattern matching** of the routing key
- Supports wildcards: `*` (single word) and `#` (multiple words)
- Use case: Selective message routing based on patterns

#### 3. Fanout Exchange
- Routes messages to **all bound queues** (broadcast)
- Ignores routing keys
- Use case: Broadcasting messages to multiple consumers

#### 4. Headers Exchange
- Routes messages based on **message headers** instead of routing keys
- More flexible but less efficient
- Use case: Complex routing logic based on headers

### Binding
- **Binding** is the link between an exchange and a queue
- Each binding can have a **routing key** (except fanout)
- Messages are routed from exchange to queues based on bindings and routing keys

## Round-Robin

RabbitMQ uses the **Round-Robin** algorithm by default to distribute messages among multiple consumers:

- Messages are distributed **sequentially** among available consumers
- If there are 2 servers: 1st message → Server 1, 2nd message → Server 2, 3rd message → Server 1, and so on
- **Proportional** workload distribution

## Benefits

### 1. Scalability
- Easily scalable: connect new servers just via URL
- Automatic load distribution across multiple servers

### 2. Single point of failure
- If one server goes down, others continue processing
- If all servers go down, messages remain **stored** in the queue for processing when they return

### 3. Decoupling
- Services are **independent** of each other
- Failure of one service does not affect others

## Security and Best Practices

### 1. TTL (Time to Live)
Lifetime of message or queue:
- **Queue TTL** (`expires`): Maximum time queue stays active without consumers
- **Message TTL** (`messageTtl`): Maximum time a message stays in the queue
- Maximum: ~49 days (4,294,967,296ms)

### 2. DLE (Dead Letter Exchange)
- Captures expired or failed messages
- Redirects to a special queue for later analysis
- Useful for debugging and monitoring

### 3. Durable and Persistent
- **Durable** (`durable: true`): Queue survives RabbitMQ restarts
- **Persistent** (`persistent: true`): Messages are saved to disk, not just RAM
- **Recommendation**: Use both to ensure messages are not lost

### 4. TLS (Transport Layer Security)
- Encryption of message traffic
- Default port: **5671**
- Recommended for production environments

### 5. AutoDelete
- `autoDelete: false` (recommended): Queue is not automatically deleted
- Prevents message loss if there are no consumers temporarily

### 6. FIFO (First In, First Out)
- Messages are processed in **arrival order**
- First message sent = first message processed
- Guaranteed processing order

## Message Architecture

### How messages are stored:
- By default: **RAM memory**
- With `persistent`: **RAM + Disk**
- When RAM fills up: old messages automatically moved to disk

## RabbitMQ in the Cloud

### CloudAMQP (example)
Free plan offers:
- 10k messages per queue
- 100 queues/topics
- 20 simultaneous connections
- 1 million total messages/month
- 3 messages/second

### Advantages:
- No server management
- Metrics and monitoring
- High availability (paid plans)

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| **Message Broker** | Communication intermediary between systems |
| **AMQP** | Protocol used by RabbitMQ |
| **Asynchronous** | Processing without waiting for immediate response |
| **Round-Robin** | Sequential message distribution |
| **Producer** | Service that sends messages |
| **Exchange** | Routes messages to queues based on routing rules |
| **Queue** | Queue that stores messages |
| **Consumer** | Service that receives messages |
| **Binding** | Link between exchange and queue |
| **Routing Key** | Key used to route messages from exchange to queue |
| **TTL** | Message/queue lifetime |
| **DLE** | Queue for dead/expired messages |
| **Durable** | Queue persists after restart |
| **Persistent** | Message saved to disk |
| **FIFO** | First in, first out |

## When to use RabbitMQ?

**Use when:**
- Need asynchronous communication
- Microservices system
- Processing that may take time
- Need horizontal scaling
- Services should be independent

**Avoid when:**
- Need immediate response (use HTTP/API)
- Very fast and simple processing
- No need for decoupling

## References

- [What is rabbitMQ?](https://medium.com/@ramonpaolo/o-que-%C3%A9-o-rabbitmq-e-como-utilizar-c3ce2406a983)
- [RabbitMQ Official](https://www.rabbitmq.com/)
- [CloudAMQP](https://www.cloudamqp.com/)
- AMQP Protocol
