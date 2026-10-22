## Task 1 : Creating and running threads
Four threads creation techniques are demonstrated:
- apollo : the  class extending Thread.
- gemini: the class implementing Runnable.
- mercury: the lambda expression.
- artemis: the virtual thread builder.

### Concurrent mode
Calling start() launches each thread separatly. The countdown output is interleaved, and its order can vary between runs."Mission control: all launched" appeared before the countdowns finished. Calling join() on all four threads ensures that"Mission control: all landed" appears after all countdowns finish.
All four final states were TERMINATED. Only artemis reported  virtual=true.
  
### Sequential mode
With --sequential, run() is called directly. The platform-thread. tasks execute one after another on the main thread.
- Apollo prints "apollo" because its code uses the Thread object's getName(). 
- Gemini and mercury print "main" because their tasks use Thread.currentThread().getName().
Calling run() directly on the virtual thread artemis does nothing, so its countdown does not appear. All four thread objects remain NEW because start() was never called. Joining these unstarted threads returns immediately.

### Interruption and coordination
Thread.sleep(200) provides the requested pause between countdown lines. It is not used to coordinate completion; join() handles that. If sleep throws InterruptedException, the catch block restores the current thread's interrupt flag and ends the countdown.


## Task 2 — Shared counters

| Counter               | Final value     | Correct | Time(ms) |
|--------------------------------------------------------------|
| UnsafeCounter         | 3,353,912       | false   | 80       |
| SynchronizedCounter   | 8,000,000       | true    | 184      |
| LockCounter           | 8,000,000       | true    | 173      |
| AtomicCounter         | 8,000,000       | true    | 112      |
| LongAdderCounter      | 8,000,000       | true    | 14       |

### Bonus — LongAdder

LongAdderCounter was the fastest safe counter in this run: 14 ms compared with AtomicCounter's 112 ms.
AtomicLong updates one shared value, so threads compete to update the same location. Under contention, LongAdder spreads updates across multiple internal values, reducing competition. Its sum() method combines those values.
sum() is not an atomic snapshot while updates are happening. Here, Bench joins all worker threads before reading the total, so the final sum is accurate. These timings scribe this run only; performance can vary between runs and machines.
- UnsafeCounter lost updates because count++ is not atomic.
- SynchronizedCounter and LockCounter protect access with locks.
- AtomicCounter uses AtomicLong to increment atomically.

### Why synchronize get() too?
If get() runs while other threads are updating the counter, an unsynchronized get() does not acquire the writers' lock. It can therefore read a stale value. Synchronizing both method on the same object protects access and ensures visibility.
In this benchmark, get() is called only after all worker threads have been joined. Successful join() calls ensure that the workers' updates are visible to the reading thread. Therefore, this benchmark would not demonstrate the visibility problem of an unsynchronized get(), even though concurrent reads would still be unsafe.

## Task 3 — Waiting and notification experiments

Each variant was run 20 times.

| Variant        | Successful | Timed out after 10 seconds | Finished with errors |
|---------------------------------------------------------------------------------|
| while+notify() | 20         | 0                          | 0                    |
| if+notifyAll() | 0          | 0                          | 20                   |

With notify(), all 20 runs succeeded. However, notify() wakes
only one arbitrary waiting thread. Because producers and consumers
share the same wait set, it can wake a thread whose condition
is still false, leaving threads that could proceed asleep.
Successful test runs do not prove this version is safe.

With if, all 20 runs had errors. NoSuchElementException was
observed when a consumer attempted to remove an order from an
empty queue after waking. An if statement does not recheck the
condition after wait() returns.

Some runs still reported 100 processed orders because surviving
consumers continued working. A correct total alone does not mean
the run was successful.

The final implementation uses while to recheck conditions after
waking and notifyAll() to wake all waiting threads.

## Task 4 — Executors, Futures, and Virtual Threads

### CPU-bound comparison

Counted primes below 4,000,000 using naive trial division.

| Method | Prime count | Time (ms) |
|---|---:|---:|
| Single thread | 283,146 | 1130 |
| Fixed pool of 16 threads, 16 tasks | 283,146 | 197 |

Both methods returned the same result. The pool was approximately
5.7 times faster in this run because tasks could execute across
multiple CPU cores.

### I/O-bound comparison

Submitted 500 simulated downloads to each executor.

| Executor | Successful | Failed | Timed out | Time (ms) |
|---|---:|---:|---:|---:|
| Fixed pool of 20 platform threads | 489 | 10 | 1 | 5435 |
| Virtual thread per task | 489 | 10 | 1 | 1044 |

Virtual threads were approximately 5.2 times faster in this run.
The fixed pool runs at most 20 downloads simultaneously, so the
remaining tasks queue. Virtual threads allow many more downloads
to wait concurrently without occupying one platform thread each.

### Failures and timeout

Every 50th download deliberately throws IOException("boom").
Future.get() reports task failures through ExecutionException.
The program catches these exceptions, records the failures,
and continues.

Download 1 sleeps for five seconds. Its Future is checked first
using get(1, TimeUnit.SECONDS). When this call times out, the
program calls cancel(true), requesting interruption. The task's
sleep responds to interruption, allowing it to stop.

### Shutdown

All executors use try-with-resources. The program prints its
completion message and returns to the terminal without System.exit().

These timings describe one run and may vary. This run used the
VS Code debugger, which can affect performance measurements.

## Task 5 — Deadlock and Graceful Shutdown

### Deadlock evidence

The broken Bank acquires account locks in transfer direction.
This allows circular waiting.

The jstack report identified transfer-0 and transfer-7:
each held an account lock required by the other.
The report is saved in deadlock.txt.

A daemon watchdog checks for deadlocks every second using
ThreadMXBean.findDeadlockedThreads(). In a separate run, it
detected transfer-0, transfer-5, and transfer-2.
The watchdog detects deadlocks but does not resolve them.

### Deadlock fixes

| Implementation | Completed attempts | Retries | Final balance | Time (ms) |
|---|---:|---:|---:|---:|
| OrderedBank | 800000 | N/A | 4000 | 129 |
| TryLockBank | 800000 | 1274 | 4000 | 4005 |

OrderedBank acquires account locks in ascending ID order.
Consistent lock ordering prevents circular waiting.

TryLockBank uses tryLock(10, MILLISECONDS). If both locks
cannot be acquired, it releases acquired locks before a random
back-off and retries. Locks are released in finally blocks.

These counts represent completed transfer attempts. Money moves
only when the source account has sufficient funds.

### Graceful shutdown

Eight workers ran for approximately two seconds.
Main interrupted all workers and called join(1000) on each.

Workers check their interrupt flag and restore it when catching
InterruptedException. Acquired locks are released during exit.

Observed results:
- Completed transfer attempts: 190995
- Retries: 701
- All eight workers alive=false
- Total shutdown time: 45 ms
- Final balance: 4000
- Correct balance: true

The program exited normally. Timings and retry counts vary
between runs.