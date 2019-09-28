# 同步与阻塞

​	同步和异步是针对应用程序和内核的交互而言的。

- 同步：执行一个操作之后，进程触发IO操作并等待(阻塞)或者轮询的去查看IO的操作(非阻塞)是否完成，等待结果，然后才继续执行后续的操作。

- 异步：执行一个操作后，可以去执行其他的操作，然后等待通知再回来执行刚才没执行完的操作。

  非阻塞是针对于进程在访问数据的时候，根据IO口的状态返回不同的状态值。阻塞方式下读取或者写入函数将一直等待，而非阻塞方式下，读取或者写入函数会立即返回一个状态值。  

- 阻塞：进程给CPU传达一个任务之后，一直等待CPU处理完成，然后才执行后面的操作。

- 非阻塞：进程给CPU传达任我后，继续处理后续的操作，隔断时间再来询问之前的操作是否完成。(轮询)

  同步异步是结果，阻塞非阻塞是手段。

​	Java IO的各种流是**同步阻塞**的。这意味着，当一个线程调用read() 或 write()时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程在此期间不能再干任何事情了。 当然，他还有一个更加重要的特性是，多路复用IO。

​	Java NIO的**同步非阻塞**模式，使一个线程从某通道发送请求读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取。而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 

​	之前使用futrueModel的就是类似异步IO模型。**异步一定是非阻塞的。**

# 概述

​	Java的NIO有三个核心部分：Channels、Buffers、Selectors

## Channel 和 Buffer

Channel有四个类型：

|      文件       | UDP                 | TCP-client        | TCP-server              |
| :-------------: | ------------------- | ----------------- | ----------------------- |
| **FileChannel** | **DatagramChannel** | **SocketChannel** | **ServerSocketChannel** |

Buffer有八个实现类型，对应八种基本数据类型除了布尔型的七种，以及一个映射类型：

| ByteBuffer      | IntBuffer         | CharIntBuffer       | FloatIntBuffer       |
| --------------- | ----------------- | ------------------- | -------------------- |
| **ShortBuffer** | **LongIntBuffer** | **DoubleIntBuffer** | **MappedByteBuffer** |

![img](./photo/channelsbuffers.png)

​	可以将一个Channel看作一个流，而buffer和channel联合起来就是一个带有缓冲区域的流。和stream不同的是，stream的方向是单向的，但是buffer和channel之间的数据传输是双向的，数据既可以从channel到buffer，也可以从buffer到channel。

## Selector 

​	Selector是一个管理器，可以管理多个channel。在线程中使用Selector，将Channel注册到Selector中，在channel中有事件就绪，就会将调用select方法，获取事件，响应事件。

# Channel

​	Channel 是一个接口，其常用的实现类有下面四个：

|      文件       | UDP                 | TCP-client        | TCP-server              |
| :-------------: | ------------------- | ----------------- | ----------------------- |
| **FileChannel** | **DatagramChannel** | **SocketChannel** | **ServerSocketChannel** |

​	一个Channel的使用实例。

```java
package com.xzj;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

//   @ author :zjxu     time:2019/1/8
public class Main {
    final static String PATH = "/Users/thisxzj/IDEAProject/Revise/NIO/src/index.txt";

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile(PATH, "rw");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        int length = fileChannel.read(byteBuffer);
        if (length != -1) {
            System.out.println("length = " + length );
            byteBuffer.flip();          //反转buffer

            while (byteBuffer.hasRemaining()) {
                System.out.print((char) byteBuffer.get());
            }
           	//byteBuffer.rewind();
            //while (byteBuffer.hasRemaining()) {
            //    System.out.print((char) byteBuffer.get());
            //}
            byteBuffer.clear();
        }
        fileChannel.close();
    }
}
```

​	运行结果：

```java
length = 12
Hello World!
```

# Buffer

Buffer是一个抽象类，他有有八个实现类型，对应八种基本数据类型除了布尔型的七种，以及一个映射类型：

| ByteBuffer      | IntBuffer         | CharIntBuffer       | FloatIntBuffer       |
| --------------- | ----------------- | ------------------- | -------------------- |
| **ShortBuffer** | **LongIntBuffer** | **DoubleIntBuffer** | **MappedByteBuffer** |

## buffer的基本用法

- 写入数据到Buffer

- 调用`flip()`方法

- 从Buffer中读取数据

- 调用`clear()`方法或者`compact()`方法

## buffer的内部

buffer内部是一个对应类型的数组，这个数字有四个index，这四个index在数据的存取过程中有自己作用。属性解释：		

1. capacity：缓冲区数字的总长度。
2. pasition：下一个要操作的数据元素的位置。
3. limit：缓冲区不可操作的下一个位置的位置。
4. mark：记录position前一个位置，default：-1(也就是不存在)。

使用方法：

1. 创建方法：ByteBuffer.allocate(n) //创建一个长度为n的Byte的缓冲区。这个时候的 capacity 和 limit的大小都是数组的长度；position的大小是0，数组的首端。
2. 在写入了数据之后，position会变为数组没有存储数据的位置的一个位置。
3. 使用byteBuffer.flip方法，limit = position，position = 0，然后就可以正确的读取这鞋数据，并且将数据发送出去。
4. 然后使用 byteBuffer.clear方法，将会到刚刚创建的状态。
5. 关于 mark 方法 ，使用mark方法的时候，mark会记录position - 1 的数据大小。当再次使用reset的时候，position将不会还原，会回到mark的值。

几个方法：

```java
public Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
}
		······
public Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
}
		······
public Buffer rewind() {
        position = 0;
        mark = -1;
        return this;
}
    	······
public final Buffer mark() {
        mark = position;
        return this;
}
		······
public Buffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
}
```

### flip方法

​	反转buffer，反转缓冲区。首先将限制设置为当前位置，然后将位置设置为 0。通常情况下，在从写入状态转向读取状态的时候调用flip方法。

### clear、compact方法

​	清除方法，清除整个缓冲区，相当于是再次初始化。和这个类似的还有一个compact方法。clear的作用是将全部缓冲区域清除，如果缓冲区中的数据全部使用结束，那么使用clear是没有问题的。

​	如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先写些数据，那么就使用compact()方法。compact()方法将所有未读的数据拷贝搬移Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。也就是将没有使用过的数据搬移到初始端，然后，在原先数据的后面接着写入新的数据。

### rewind 方法

​	无论是在读还是写的模式下，都从头开始。如果是读的模式，使用这个方法，写入新的数据将会覆盖原来数据的。如果是读取的话，将会重复的将原先读过的数据在读一遍。

​	如将channel中的注释打开，那么执行的结果就是：

```java
length = 12
Hello World!Hello World!
```

​	这就体现了在读取的时候rewind的作用。

### mark()与reset()方法

​	通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。

​	例如，先mark一下，然后经过读或者是写，之后，使用reset可以将position返回到使用mark的时候的那个mark值。但是flip、clear、compact、rewind、reset都会将mark标记回归负一，也就是取消掉。在使用reset的时候，必须要有一个mark，否则会exception。

## buffer的使用

### 创建

​	使用Buffer抽象类的实现类的静态方法来创建，例如：

```java
//创建一个对应的Buffer的对象，其最大容量为128。
XXXBuffer xxxBuffer = XXXBuffer.allocate(128);
```



### 写数据

​	**从channel中：**

```java
int readLength = inChannel.read(xxxBuffer); 
```

​	使用channel的read方法，将channel中的数据传入xxxBuffer中。

​	**使用put方法：**

```java
xxxBuffer.put(new byte[]{' ', 'x', ' ', 'z', ' ', 'j', ' ', '!'});
```

​	使用buffer对象的put方法，将put方法的参数，传入的到buffer中。这个参数可以是这个buffer的类型的值、数组。还可以是另一个同类型的buffer，这个参数的本质也是一个数组。

### 读取数据

​	**从Buffer读取数据到Channel：**

```java
int writeLength = inChannel.write(xxxBuffer); 
```

​	**使用get方法获取buffer中的数据：** 

```java
xxxBuffer.get();			//获取的是一个xxx类型的数据
```

### 其他

​	可以使用equare 和 compareTo来比较两个buffer。

# 多buffer操作

​	使用多个buffer，对应一个channel。

### scatter

​	使用buffer的数组从channel中获取信息的时候，将会按照数组中buffer的顺序，将channel中的信息，保存在buffer中。

![](./photo/scatter.png)

```java
package com.xzj;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

//   @ author :zjxu     time:2019/1/8
public class Main {
    final static String PATH = "/Users/thisxzj/IDEAProject/Revise/NIO/src/index.txt";

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile(PATH, "rw");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(10);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
        ByteBuffer[] byteBuffer = new ByteBuffer[]{byteBuffer1, byteBuffer2};
        long length = fileChannel.read(byteBuffer);

        if (length != -1) {
            System.out.println("length = " + length);
            byteBuffer2.flip();          //反转buffer
            while (byteBuffer2.hasRemaining()) {
                System.out.print((char) byteBuffer2.get() );
            }
            byteBuffer2.clear();
        }
        fileChannel.close();
    }
}
```

​	index.txt中保存的文本：

```
Hello World!Hello World!
```

​	程序的输出：

```
length = 24
d!Hello World!
```

​	很明显，第一个长度为10的数组中，保存了前面十个字符。后面长度为100的保存了后面的十四位。在移动下一个buffer前，必须填满当前的buffer。

### gather

​	同样的，channel在获取数字的时候，可以有这样的数组的机制。但是需要注意的是，只有position和limit之间的数字会被填充进去。所以和前面的一样，需要使用filp更改来将数据刷新进去。在满的时候，或者是将队尾的空格输入进去的时候，使用rewind。

![](./photo/gather.png)





```java
package com.xzj;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

//   @ author :zjxu     time:2019/1/8
public class Main {
    final static String PATH = "/Users/thisxzj/IDEAProject/Revise/NIO/src/index.txt";

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile(PATH, "rw");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(10);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(10);

        byteBuffer1.put("0123456789".getBytes());
        byteBuffer1.rewind();
        byteBuffer2.put("ABCDEFGHIJ".getBytes());
        byteBuffer2.rewind();

        ByteBuffer[] byteBuffer = new ByteBuffer[]{byteBuffer1, byteBuffer2};

        long length = fileChannel.write(byteBuffer);
        System.out.println(length);
        fileChannel.close();
    }
}
```

​	index.txt中的文本：

```java
0123456789ABCDEFGHIJ
```

​	控制台信息：

```java
20
```

# 通道间通信

## transferFrom

​	将from中文本信息复制到to中，如果to中文本信息长度比from长，多余的一部分保留。

```java
package com.xzj;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

//   @ author :zjxu     time:2019/1/8
public class Main {
    final static String FROM = "/Users/thisxzj/IDEAProject/Revise/NIO/src/fromIndex.txt";
    final static String TO = "/Users/thisxzj/IDEAProject/Revise/NIO/src/toIndex.txt";

    public static void main(String[] args) throws IOException {
        RandomAccessFile fromFile = new RandomAccessFile(FROM, "rw");
        RandomAccessFile toFile = new RandomAccessFile(TO, "rw");
        FileChannel fromChannel = fromFile.getChannel();
        FileChannel toChannel = toFile.getChannel();

        long position = 0;
        long count = fromChannel.size();

        toChannel.transferFrom(fromChannel, position, count);
        fromChannel.close();
        toChannel.close();
    }
}
```

​	运行结果：

```
fromIndex：abcdbfghijkrmnopqrstuvwxyz
toIndex：1234567890123456789012345678901234567890

运行之后，from中文本不变，to中的文本变化如下：
toIndex：abcdbfghijkrmnopqrstuvwxyz78901234567890
```

## transferTo

​	将上面的程序的第21行改为：

```java
       fromChannel.transferTo(position, count, toChannel);
```

其余的逻辑，方法都不变。



# SelectableChannel

​	和selector配套使用的是selectable接口的实现类，通常是网络接口。所以，NIO是和网络紧密相关的一个IO机制。

- 有关UDP协议的：DatagramChannel。
- 有关SCTP协议的：SctpChannel、SctpMultiChannel、SctpServerChannel。
- 有关TCP协议的：ServerSocketChannel、SocketChannel。
- 有关管道的：SinkChannel、SourceChannel这两个抽象类定义在java.nio.channels.Pipe类中。

- Socket和ServerSocket是位于java.net下的两个类。
- SocketChannel和ServerSocketChannel是位于java.nio.channels下的两个类。

连接关系：

- 服务器必须先建立ServerSocket或者ServerSocketChannel 来等待客户端的连接。
- 客户端必须建立相对应的Socket或者SocketChannel来与服务器建立连接。
- 服务器接受到客户端的连接，再生成一个Socket或者SocketChannel与此客户端通信。

## SocketChannel

#### 打开方法

```java
//客户端
SocketChannel socketChannel = SocketChannel.open();
socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));
```

#### 连接方法

```java
//客户端
socketChannel.configureBlocking(false);  //非阻塞模式
socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));
```

#### 写入方法

```java
//客户端
ByteBuffer writeBuffer = ByteBuffer.allocate(64);
writeBuffer.clear();
writeBuffer.put((new Date().toString()).getBytes());
writeBuffer = writeBuffer.flip();
socketChannel.write(writeBuffer);
```

#### 读取方法

```java
//客户端
readBuff.clear();
int length = socketChannel.read(readBuff);
readBuff.flip();
System.out.println("received: " +
              new String(readBuff.array()).substring(0, length));
```

#### 关闭方法

```java
//客户端
socketChannel.close();
```



## ServerSocketChannel

#### 打开方法

```java
//服务端
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
ssc.configureBlocking(false);
```

#### 连接方法

```java
//服务端
ssc.configureBlocking(false);				//非阻塞模式
ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
```

#### 监听连接

​	通过 ServerSocketChannel.accept() 方法监听新进来的连接。当 accept()方法返回的时候,它返回一个包含新进来的连接的 SocketChannel。因此, accept()方法会一直阻塞到有新连接到达。

​	通常不会仅仅只监听一个连接,在while循环中调用 accept()方法。如下面的例子：

```java
while(true){
    SocketChannel socketChannel = serverSocketChannel.accept();
    //do something with socketChannel...
}
```

​	当然,也可以在while循环中使用除了true以外的其它退出准则。

#### 读取方法

```java
//使用一个新建的Socket连接，在这个连接上使用read和write
readBuff.clear();
int length = socketChannel.read(readBuff);
readBuff.flip();
System.out.println("received: " +
              new String(readBuff.array()).substring(0, length));
```

#### 关闭方法

```java
//服务端
serverSocketChannel.close();
```

## TCP的使用示例

#### WebServer

```java
package com.xzj;

//   @ author :zjxu     time:2019/1/9

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class WebServer {
    public static void main(String[] args) throws IOException {

        //创建一个channel并且设定为非阻塞模式
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
        serverSocketChannel.configureBlocking(false);

        //创建selector
        Selector selector = Selector.open();

        //将channel和selector关联起来，并且指定感兴趣的事件是 Accept
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //创建读写buffer
        ByteBuffer readBuff = ByteBuffer.allocate(1024);
        ByteBuffer writeBuff = ByteBuffer.allocate(128);

        writeBuff.put("enter:".getBytes());
        writeBuff.flip();

        while (true) {
            int nReady = selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
               //轮询
                SelectionKey key = it.next();
                it.remove();

                if (key.isAcceptable()) {
                    // 创建新的连接，并且把连接注册到selector上，而且
                    // 声明这个channel只对读操作感兴趣
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    readBuff.clear();
                    int length = socketChannel.read(readBuff);

                    readBuff.flip();
                    System.out.println("received: " + new String(readBuff.array()).substring(0, length-1));
                    key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    writeBuff.rewind();
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.write(writeBuff);
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
```

#### WebClient

```java
package com.xzj;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

//   @ author :zjxu     time:2019/1/9
public class WebClient {
    public static void main(String[] args) throws IOException {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));

           	//创建读写buffer
            ByteBuffer writeBuffer = ByteBuffer.allocate(64);
            ByteBuffer readBuffer = ByteBuffer.allocate(32);

            while (true) {
                writeBuffer.clear();
                writeBuffer.put((new Date().toString()).getBytes());
                writeBuffer.flip();
                socketChannel.write(writeBuffer);
                readBuffer.clear();
                socketChannel.read(readBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## DatagramChannel

#### 打开方法

```java
DatagramChannel channel = DatagramChannel.open();
```

#### 连接方法

```java
channel.socket().bind(new InetSocketAddress(8000));
```

#### 收发方法

```java
ByteBuffer sendBuffer = ByteBuffer.allocate(64);
ByteBuffer receiveBuffer = ByteBuffer.allocate(64);

//接收方法
receiveBuffer.clear();
channel.receive(receiveBuffer);

String string =
        new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
//发送方法
sendBuffer.clear();
sendBuffer.put(string.getBytes());
sendBuffer.flip();
channel.send(sendBuffer,new InetSocketAddress(8001));
```

​	可以将DatagramChannel“连接”到网络中的特定地址的。由于UDP是无连接的，连接到特定地址并不会像TCP通道那样创建一个真正的连接。而是锁住DatagramChannel ，让其只能从特定地址收发数据。

​	这里有个例子:

```java
`channel.connect(``new` `InetSocketAddress(``"jenkov.com"``, ``80``));`
```

​	当连接后，也可以使用read()和write()方法，就像在用传统的通道一样。只是在数据传送方面没有任何保证。这里有几个例子：

```java
int bytesRead = channel.read(buf);
int bytesWrite = channel.write(buf);
```

## UDP的使用示例

```java
package com.xzj;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

//   @ author :zjxu     time:2019/1/9
public class DatagramTest {
    public void send() throws IOException {
        DatagramChannel dChannel = DatagramChannel.open();
        dChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String string = scanner.next();
            buffer.put((new Date().toString()+">>"+string).getBytes());
            buffer.flip();
            dChannel.send(buffer, new InetSocketAddress("127.0.0.1", 8989));
            buffer.clear();

        }
        dChannel.close();
    }
    public void receive() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(8989));
        Selector selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);
        while(selector.select()>0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                if (selectionKey.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(),0,buffer.limit()));
                    buffer.clear();
                }
            }
            iterator.remove();
        }


    }
}
```

## Pipe

​	JavaNIO中的pipe是两个**线程之间**的单向数据连接。Pipe有一个source通道和一个sink通道。数据会被写到sink通道，从source通道读取。

### 创建管道

```java
Pipe pipe = Pipe.open();
```

### 写入

```java
SinkChannel sinkChannel = pipe.sink();
ByteBuffer byteBuffer = ByteBuffer.allocate(64);

String string =
        new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

byteBuffer.clear();
byteBuffer.put(string.getBytes());
byteBuffer.flip();

sinkChannel.write(byteBuffer);
```

### 读取

```java
SourceChannel sourceChannel = pipe.source();
ByteBuffer readBuffer = ByteBuffer.allocate(64);

readBuffer.clear();
int length = sourceChannel.read(readBuffer);
System.out.println(new String(readBuffer.array()).substring(0, length - 1));
sourceChannel.close();
```

## Pipe使用示例

```java
package com.xzj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


//   @ author :zjxu     time:2019/1/9
public class Test {
    public static void main(String[] args) throws IOException {
        Pipe pipe = Pipe.open();
        SinkChannel sinkChannel = pipe.sink();
        ByteBuffer writeBuffer = ByteBuffer.allocate(64);

        String string =
                new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()) + "\n";

        writeBuffer.clear();
        writeBuffer.put(string.getBytes());
        writeBuffer.flip();

        sinkChannel.write(writeBuffer);
        sinkChannel.close();

        SourceChannel sourceChannel = pipe.source();
        ByteBuffer readBuffer = ByteBuffer.allocate(64);

        readBuffer.clear();
        int length = sourceChannel.read(readBuffer);
        System.out.println(new String(readBuffer.array()).substring(0, length - 1));
        sourceChannel.close();
    }
}
```

​	运行结果：

```java
20190109 12:34:14
```

# Selector

​	使用scelector的好处是，只需要更少的线程来维护通道。，可以使用以恶搞线程处理所有的通道。

### 创建方法

```java
Selector selector = Selector.open();
 //selector.isOpen();判断是否打开
 //selector.close();关闭
```

### 注册通道

```java
channel.configureBlocking(false);
SelectionKey readKey = channel.register(selector,SelectionKey.OP_READ);
```

第二个参数，表示监听的动作：

有四个类型：

|   事件   |  符号   |         key常量         |
| :------: | :-----: | :---------------------: |
| 连接就绪 | Connect | SelectionKey.OP_CONNECT |
| 接收就绪 | Accept  | SelectionKey.OP_ACCEPT  |
|  读就绪  |  Read   |  SelectionKey.OP_READ   |
|  写就绪  |  Write  |  SelectionKey.OP_WRITE  |

### Key

​	使用key常量将selector和channel中的动作关联起来，并返回一个SelectionKey对象。只要是channel向selector中注册了事件，selector就会跟踪检测这个事件是否发生。这些的key对象就是检测这些事件的句柄。

#### 获取keys

```java
//所有注册到selector的key
Set<SelectionKey> keys = selector.keys();
//相关事件被捕获的key
Set<SelectionKey> selectedKeys = selector.selectedKeys();
```

​	如果关闭了与SelectionKey对象关联的Channel对象，或者调用了SelectionKey对象的cancel方法，这个SelectionKey对象就会被加入到cancelled-keys集合中，表示这个SelectionKey对象已经被取消。

​	cancelled-keys没有对应的方法被获取。

#### interest集合

```java
int interestSet = key.interestOps();
boolean isInterestedInAccept = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
boolean isInterestedInConnect = (interestSet & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT;
boolean isInterestedInRead = (interestSet & SelectionKey.OP_READ) == SelectionKey.OP_READ;
boolean isInterestedInWrite = (interestSet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
```

用来检测对那些事件感兴趣。其中：

| 操作类型   | 值     | 描述           |
| ---------- | ------ | -------------- |
| OP_READ    | 1 << 0 | 读操作         |
| OP_WRITE   | 1 << 2 | 写操作         |
| OP_CONNECT | 1 << 3 | 连接socket操作 |
| OP_ACCEPT  | 1 << 4 | 接受socket操作 |

​	使用interestOps();方法获取的是一个多位二进制数，通过逻辑操作，可以判断是不是在key上有对应的操作。

#### ready集合

```java
int readySet = key.readyOps();
boolean isAcceptable = (readySet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
boolean isConnectable = (readySet & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT;
boolean isReadable = (readySet  & SelectionKey.OP_READ) == SelectionKey.OP_READ;
boolean isWritable =  (readySet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
```

​	和上面的获取机制一模一样。此外还可以使用直接获取的方式，获取ready状态。

```java
key.isAcceptable();
key.isConnectable();
key.isReadable();
key.isWritable();
```

#### 附加对象

​	通过key生成的channel和seletor：

```java
Channel keyChannel = key.channel();
Selector keySelector = key.selector();
```

​	将一个对象添加到key上：

```java
StringBuilder stringBuilder = new StringBuilder("xzj");
//添加上去
key.attach(stringBuilder);
//从key获取
StringBuilder getter = (StringBuilder) key.attachment();
```



## select()

​	向一个selector中注册多个channel之后，可以使用selector的select方法，该方法的返回值是一个int型，表示的是通道就绪的数量。使用的select()方法是阻塞等待至少一个通道准备就绪，其中可以向其中添加一个最长等待时间，也可以使用selectNow()来表示立即返回。

​	如下：

```java
selector.select();
selector.select(1000);
selector.selectNow();
```

## 遍历

```java
Set<SelectionKey> selectedKeys = selector.selectedKeys();
Iterator selectedKeysIterator = selectedKeys.iterator();

while (selectedKeysIterator.hasNext()) {
    SelectionKey key = (SelectionKey) selectedKeysIterator.next();
    if (key.isAcceptable()) {      // a connection was accepted by a ServerSocketChannel

    } else if (key.isConnectable()) {     // a connection was established with a remote server

    } else if (key.isReadable()) {        // a channel is ready for reading

    } else if (key.isWritable()) {        // a channel is ready for writing

    }

    //将刚刚访问过的元素删除
    selectedKeysIterator.remove();
}
```



## weakUp方法

​	在某一个线程在使用select()方法被阻塞，而无法返回的时候，在同一个select对象上使用weakUp()方法，会使阻塞的select方法返回。