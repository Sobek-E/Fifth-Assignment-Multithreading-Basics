### 1. `start()` vs `run()`

```java  
public class StartVsRun {    
    static class MyRunnable implements Runnable {    
        public void run() {    
            System.out.println("Running in: " + Thread.currentThread().getName()); 
        }    
    }    
    public static void main(String[] args) throws InterruptedException {    
        Thread t1 = new Thread(new MyRunnable(), "Thread-1");    
        System.out.println("Calling run()");    
        t1.run();    
        Thread.sleep(100);    
    
        Thread t2 = new Thread(new MyRunnable(), "Thread-2");    
        System.out.println("Calling start()");    
        t2.start();    
    }  
}  
```  

**Questions:**

- What output do you get from the program? Why?

Calling run()
Running in: main
Calling start()
Running in: Thread-2

cause  thread1 used run and thread2 used start

- What’s the difference in behavior between calling `start()` and `run()`?

when we use run it doesn't make new thread and it continue the main but when we use start it makes new thread and run the program

---  

### 2. Daemon Threads

```java  
public class DaemonExample {    
    static class DaemonRunnable implements Runnable {    
        public void run() {    
            for(int i = 0; i < 20; i++) {    
                System.out.println("Daemon thread running...");    
                try {    
                    Thread.sleep(500);    
                } catch (InterruptedException e) {    
                 //[Handling Exception...]  
                }            
            }    
        }    
    }    
    public static void main(String[] args) {    
        Thread thread = new Thread(new DaemonRunnable());    
        thread.setDaemon(true);    
        thread.start();    
        System.out.println("Main thread ends.");    
    }  
}  
```  

**Questions:**
- What output do you get from the program? Why?

Daemon thread running...
Main thread ends.
.
.
.
it can be different 
because it not real thread and when main execute it will be executed too


- What happens if you remove `thread.setDaemon(true)`?

it would be like real thread 

- What are some real-life use cases of daemon threads?

Garbage Collector/background sync/ log writer
  
---  

### 3. A shorter way to create threads

```java  
public class ThreadDemo {  
    public static void main(String[] args) {  
        Thread thread = new Thread(() -> {  
            System.out.println("Thread is running using a ...!");  
        });  
  
        thread.start();  
    }  
}   
```  

**Questions:**
- What output do you get from the program?

Thread is running using a ...!

- What is the `() -> { ... }` syntax called?

Lambda Expression

- How is this code different from creating a class that extends `Thread` or implements `Runnable`?

we dont need to make a new class and its faster
