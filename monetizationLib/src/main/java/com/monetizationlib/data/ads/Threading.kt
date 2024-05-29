package com.monetizationlib.data.ads

import com.google.android.gms.common.util.concurrent.NamedThreadFactory
import java.util.*
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class Threading private constructor(
    threadPoolName: String,
    defaultThreadPoolSize: Int,
    maximumPoolSize: Int
) :
    ThreadPoolExecutor(
        defaultThreadPoolSize,
        maximumPoolSize,
        20,
        TimeUnit.SECONDS,
        ThreadPoolExecutorPriorityQueue(1, ThreadingComparator())
    ) {

    constructor() : this("One", 4, 4 * 2) {}

    init {
        threadFactory = NamedThreadFactory(threadPoolName)
        rejectedExecutionHandler = BlockingQueuePut()
    }

    private class ThreadingComparator : Comparator<Runnable> {
        override fun compare(left: Runnable, right: Runnable): Int {
            var leftPriority = Thread.NORM_PRIORITY
            var rightPriority = Thread.NORM_PRIORITY
            if (left is PriorityRunnable) {
                leftPriority = left.priority
            }
            if (right is PriorityRunnable) {
                rightPriority = right.priority
            }
            return leftPriority - rightPriority
        }
    }

    private class PriorityRunnable internal constructor(
        val priority: Int,
        private val runnable: Runnable
    ) : Runnable {

        override fun run() {
            runnable.run()
        }
    }

    private class ThreadPoolExecutorPriorityQueue(
        initialCapacity: Int,
        threadingComparator: ThreadingComparator
    ) :
        PriorityBlockingQueue<Runnable>(initialCapacity, threadingComparator) {

        override fun offer(runnable: Runnable): Boolean {
            return if (size <= 0) {
                super.offer(runnable)
            } else {
                false
            }
        }

        fun forceOffer(runnable: Runnable) {
            super.offer(runnable)
        }
    }

    private inner class BlockingQueuePut : RejectedExecutionHandler {
        override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
            if (this@Threading.isShutdown) {

                return
            }
            (executor.queue as ThreadPoolExecutorPriorityQueue).forceOffer(r)
        }
    }

    override fun execute(command: Runnable) {
        try {
            super.execute {
                try {
                    command.run()
                } catch (ex: Throwable) {

                }
            }
        } catch (throwable: Throwable) {
        }
    }

    companion object {
        private val lock = Any()
        private var instance: Threading? = null

        fun getInstance(): Threading {
            synchronized(lock) {
                if (instance == null) {
                    instance = Threading()
                }
                return instance as Threading
            }
        }

        fun create(
            threadPoolName: String,
            defaultThreadPoolSize: Int,
            maximumPoolSize: Int
        ): Threading {
            return Threading(threadPoolName, defaultThreadPoolSize, maximumPoolSize)
        }
    }
}
