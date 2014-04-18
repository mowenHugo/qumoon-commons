/*
 * Copyright (c) 2013 Raycloud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qumoon.commons;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 */
public class BlockingThreadPoolExecutor extends ThreadPoolExecutor {

  private Semaphore semaphore;

  public BlockingThreadPoolExecutor(int bound) {
    super(bound, bound, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(bound));
    this.semaphore = new Semaphore(bound);
  }

  @Override
  public void execute(Runnable task) {
    boolean acquired = false;
    do {
      try {
        semaphore.acquire();
        acquired = true;
      } catch (InterruptedException e) {
        // wait forever!
      }
    } while (!acquired);

    try {
      super.execute(task);
    } catch (RuntimeException e) {
      // specifically, handle RejectedExecutionException
      semaphore.release();
      throw e;
    } catch (Error e) {
      semaphore.release();
      throw e;
    }
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    semaphore.release();
  }


  public static void main(String[] args) {
    ThreadPoolExecutor executor = new BlockingThreadPoolExecutor(2);
    while (true) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          System.out.println("bingo");
          try {
            Thread.sleep(1000 * 5);
          } catch (InterruptedException e) {

          }
        }
      });
      System.out.println("latch");
    }
  }
}
