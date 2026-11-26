# 🦁 ZooKeeper Distributed Systems Demo

> A hands-on Java demonstration of **Apache ZooKeeper** concepts — distributed configuration management and leader election — built from scratch without any build tools.

---

## 📖 What Is This?

This project demonstrates two core distributed systems patterns using **Apache ZooKeeper**:

| Demo | What It Shows |
|------|--------------|
| **Config Management** | Store a config value in ZooKeeper and watch it update in real-time across multiple clients |
| **Leader Election** | Multiple workers compete to become the leader — if the leader dies, a follower instantly takes over |

These patterns are used in production systems like **Kafka**, **HBase**, **Hadoop**, and **Solr**.

---

## 🗂️ Project Structure

```
zkcode/
├── ZkConnect.java          # Helper: opens & waits for a ZooKeeper session
├── ConfigPublisher.java    # Writes / updates /demo/config in ZooKeeper
├── ConfigWatcher.java      # Watches /demo/config and prints every change live
├── ElectionWorker.java     # Joins an election group; becomes leader or follows
├── lib/                    # ZooKeeper client JARs (download instructions below)
│   ├── zookeeper-3.9.3.jar
│   ├── zookeeper-jute-3.9.3.jar
│   ├── slf4j-api-1.7.36.jar
│   ├── slf4j-simple-1.7.36.jar
│   ├── netty-*.jar
│   └── ...
└── README.md
```

---

## ⚙️ Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 11 or later | [Adoptium Temurin](https://adoptium.net) |
| Apache ZooKeeper | 3.8+ | [zookeeper.apache.org](https://zookeeper.apache.org/releases.html) |

> **Windows users:** This guide uses `cmd.exe` commands. Replace `;` with `:` for the classpath separator on **Linux/macOS**.

---

## 🚀 Setup Guide

### Step 1 — Download ZooKeeper Client JARs

Create a `lib/` folder and download these JARs from [Maven Central](https://mvnrepository.com):

```
lib/zookeeper-3.9.3.jar
lib/zookeeper-jute-3.9.3.jar
lib/slf4j-api-1.7.36.jar
lib/slf4j-simple-1.7.36.jar
lib/netty-buffer-4.1.100.Final.jar
lib/netty-codec-4.1.100.Final.jar
lib/netty-common-4.1.100.Final.jar
lib/netty-handler-4.1.100.Final.jar
lib/netty-resolver-4.1.100.Final.jar
lib/netty-transport-4.1.100.Final.jar
lib/netty-transport-native-epoll-4.1.100.Final.jar
```

Or use `curl` to download them all at once:

```bash
mkdir lib && cd lib

curl -O https://repo1.maven.org/maven2/org/apache/zookeeper/zookeeper/3.9.3/zookeeper-3.9.3.jar
curl -O https://repo1.maven.org/maven2/org/apache/zookeeper/zookeeper-jute/3.9.3/zookeeper-jute-3.9.3.jar
curl -O https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
curl -O https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-buffer/4.1.100.Final/netty-buffer-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-codec/4.1.100.Final/netty-codec-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-common/4.1.100.Final/netty-common-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-handler/4.1.100.Final/netty-handler-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-resolver/4.1.100.Final/netty-resolver-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-transport/4.1.100.Final/netty-transport-4.1.100.Final.jar
curl -O https://repo1.maven.org/maven2/io/netty/netty-transport-native-epoll/4.1.100.Final/netty-transport-native-epoll-4.1.100.Final.jar
```

---

### Step 2 — Install & Start ZooKeeper Server

**Download** the ZooKeeper binary from the [Apache releases page](https://zookeeper.apache.org/releases.html) and extract it.

**Configure** it:
```bash
# Inside the ZooKeeper directory:
cp conf/zoo_sample.cfg conf/zoo.cfg
```

**Start the server:**

```bash
# Linux / macOS
bin/zkServer.sh start

# Windows
bin\zkServer.cmd start
```

> ✅ ZooKeeper is running when you see:
> ```
> binding to port 0.0.0.0/0.0.0.0:2181
> ```

---

### Step 3 — Compile the Java Files

Navigate to the `zkcode/` folder and compile all four files:

**Windows:**
```cmd
javac -cp "lib\*" -d . ZkConnect.java ConfigPublisher.java ConfigWatcher.java ElectionWorker.java
```

**Linux / macOS:**
```bash
javac -cp "lib/*" -d . ZkConnect.java ConfigPublisher.java ConfigWatcher.java ElectionWorker.java
```

> You should now see `.class` files next to the `.java` files. No errors = success ✅

---

## 🎬 Running the Demo

> **Open 5 separate terminal windows** and run each command in the order shown.

---

### 🟩 Terminal 1 — ZooKeeper Server

```bash
# Linux / macOS
bin/zkServer.sh start-foreground

# Windows
bin\zkServer.cmd start
```

**Wait for:** `binding to port 0.0.0.0/0.0.0.0:2181`

> 🔒 **Keep this terminal open for the entire demo.**

---

### 🟦 Terminal 2 — ConfigWatcher *(start BEFORE the publisher)*

**Windows:**
```cmd
java -cp ".;lib\*" ConfigWatcher localhost:2181
```

**Linux / macOS:**
```bash
java -cp ".:lib/*" ConfigWatcher localhost:2181
```

**Expected output:**
```
[session] SyncConnected
config = hello-world (version 0)
```

> 👀 **Watch this terminal** — every time you run ConfigPublisher, a new line appears here instantly!

---

### 🟨 Terminal 3 — ConfigPublisher *(run this multiple times!)*

**First run — creates the config node:**

```cmd
# Windows
java -cp ".;lib\*" ConfigPublisher localhost:2181 hello-world
```
```bash
# Linux / macOS
java -cp ".:lib/*" ConfigPublisher localhost:2181 hello-world
```

**Expected output:**
```
[session] SyncConnected
created /demo/config = hello-world
```

**Second run — updates the value:**
```cmd
java -cp ".;lib\*" ConfigPublisher localhost:2181 my-new-config
```
**Expected output:**
```
updated /demo/config = my-new-config
```

**Third run:**
```cmd
java -cp ".;lib\*" ConfigPublisher localhost:2181 final-value
```

> 🔔 Each time you run this, **Terminal 2 (Watcher)** prints the updated value live!

---

### 🟥 Terminal 4 — ElectionWorker: Alice

**Windows:**
```cmd
java -cp ".;lib\*" ElectionWorker Alice localhost:2181
```

**Linux / macOS:**
```bash
java -cp ".:lib/*" ElectionWorker Alice localhost:2181
```

**Expected output:**
```
[session] SyncConnected
Alice: created /demo/election/n_0000000001
Alice: I am the LEADER
```

> 👑 Alice is first → she is the **LEADER**!

---

### 🟪 Terminal 5 — ElectionWorker: Bob

**Windows:**
```cmd
java -cp ".;lib\*" ElectionWorker Bob localhost:2181
```

**Linux / macOS:**
```bash
java -cp ".:lib/*" ElectionWorker Bob localhost:2181
```

**Expected output:**
```
[session] SyncConnected
Bob: created /demo/election/n_0000000002
Bob: follower, watching n_0000000001
```

> Bob arrives second → he watches Alice and waits.

---

### 🔪 The Magic Moment — Kill the Leader!

Press **`Ctrl + C`** in **Terminal 4** (Alice's terminal) to simulate a crash.

> **Immediately watch Terminal 5 (Bob):**
> ```
> Bob: I am the LEADER
> ```

Bob detects that Alice's ephemeral znode disappeared and **instantly promotes himself to leader**. This is ZooKeeper's leader election working in real time — no polling, no delay, just an event-driven watch callback!

You can add a third worker (Charlie) in a 6th terminal the same way and repeat the experiment.

---

## 🧠 How It Works

### ConfigPublisher & ConfigWatcher

```
ConfigPublisher                    ZooKeeper Server              ConfigWatcher
     │                                    │                            │
     │── create /demo/config ──────────► │                            │
     │                                    │── NodeDataChanged ────────►│
     │                                    │                            │── getData() ──► prints new value
     │                                    │                            │── re-registers watch
     │── setData /demo/config ──────────►│                            │
     │                                    │── NodeDataChanged ────────►│
     │                                    │                            │── prints new value again
```

### Leader Election

```
Alice registers n_0000000001  →  smallest node  →  LEADER
Bob   registers n_0000000002  →  watches n_0000000001 (Alice)
Charlie registers n_0000000003 →  watches n_0000000002 (Bob)

If Alice crashes → n_0000000001 deleted → Bob gets watch event → Bob is now LEADER
If Bob crashes  → n_0000000002 deleted → Charlie gets watch event → Charlie is now LEADER
```

> This avoids the **"herd effect"** — only one follower is notified per failure, not all of them.

---

## 📋 ZooKeeper Concepts Covered

| Concept | Used In |
|---------|---------|
| **Znodes** | Both demos — `/demo/config`, `/demo/election/n_*` |
| **Watchers** | ConfigWatcher re-registers a watch on every read |
| **Ephemeral nodes** | ElectionWorker — node deleted when session closes |
| **Sequential nodes** | ElectionWorker — `n_0000000001`, `n_0000000002`, ... |
| **Sessions** | Every client has a session; closing it deletes ephemeral nodes |
| **SyncConnected** | ZkConnect waits for this state before returning |

---

## 🛠️ Troubleshooting

| Problem | Fix |
|---------|-----|
| `Connection refused` on port 2181 | ZooKeeper server is not running — start Terminal 1 first |
| `ClassNotFoundException` | Make sure you compiled with `-cp "lib/*"` and `.class` files exist |
| `NodeExistsException` | The znode already exists from a previous run — safe to ignore |
| `javac: command not found` | Java JDK not installed or not on PATH |
| Watcher does not fire | Make sure you started ConfigWatcher **before** running ConfigPublisher |

---

## 📚 Further Reading

- [Apache ZooKeeper Documentation](https://zookeeper.apache.org/doc/current/)
- [ZooKeeper: Wait-free coordination for Internet-scale systems (Original Paper)](https://www.usenix.org/legacy/event/atc10/tech/full_papers/Hunt.pdf)
- [ZooKeeper Recipes — Leader Election](https://zookeeper.apache.org/doc/current/recipes.html#sc_leaderElection)

---

## 👨‍💻 Author

**GUGULOTH SRISHANTH**
- Distributed Computing Systems — ZooKeeper Lab
- Java 21 · ZooKeeper 3.9.3 · Windows 11

---

## 📄 License

This project is for educational purposes.
