package vn.flast.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ActionThread {

    public static final long NO_SLEEP = -1L;
    public static final long EXCEPTION_SLEEP_TIME = 10000L;
    private Thread thread = null;
    private String name;

    public ActionThread(String name) {
        this.name = name;
    }

    public ActionThread() {
        this.name = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        if (null != this.thread) {
            this.thread.setName(this.name);
        }
    }

    private boolean needToDie = false;
    private boolean noNeedToDie() {
        return !this.needToDie;
    }

    public synchronized void execute() {
        if (null == this.thread) {
            this.thread = new Thread(new ActionThreadImpl(this), this.name);
            this.needToDie = false;
            this.thread.setName(this.name);
            this.thread.start();
            for (; !this.thread.isAlive(); Thread.yield()) {
                var thread = Thread.currentThread();
                log.info("====== Thread.yield name: {}", thread.getName());
            }
        }
    }

    public synchronized void kill() {
        this.needToDie = true;
        try {
            if (Thread.currentThread() != this.thread) {
                while (this.thread.isAlive()) {
                    if (!this.thread.isInterrupted()) {
                        this.thread.interrupt();
                    }
                    this.thread.join();
                }
            }
        } catch (InterruptedException ignored) {

        } finally {
            this.thread = null;
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }

    protected abstract void onKilling();
    protected abstract void onException(Exception ex);
    protected abstract long sleepTime();
    protected abstract void action();

    private record ActionThreadImpl(ActionThread ref) implements Runnable {
        @Override
        public void run() {
            do {
                try {
                    while (this.ref.noNeedToDie()) {
                        this.ref.action();
                        long milliseconds = this.ref.sleepTime();
                        if (NO_SLEEP != milliseconds) {
                            this.ref.sleep(milliseconds);
                        }
                    }
                } catch (Exception e) {
                    this.ref.onException(e);
                    this.ref.sleep(EXCEPTION_SLEEP_TIME);
                }
            } while (this.ref.noNeedToDie());
            this.ref.onKilling();
        }
    }
}
