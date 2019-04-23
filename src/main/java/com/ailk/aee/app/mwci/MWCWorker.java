package com.ailk.aee.app.mwci;


import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.common.conf.util.XMLInputStreamParser;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.util.NumberUtils;
import com.ailk.aee.core.Worker;
import com.ailk.aee.eventgenerator.BlockLoaderEventGenerator;
import com.ailk.aee.eventgenerator.TableDataEventLoader;
import com.ailk.aee.eventgenerator.UnionBlockQueueEventGenerator;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.strategy.ICounterStrategy;
import com.ailk.aee.strategy.StaticWorkCounterStrategy;
import com.ailk.aee.worker.EventDrivenThreadPoolWorker;
import com.ailk.aee.worker.EventDrivenWorker;
import com.ailk.aee.worker.SelfConstructedWorker;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import org.apache.commons.collections.map.HashedMap;

import java.util.Iterator;
import java.util.Map;

public class MWCWorker extends SelfConstructedWorker {
    private String groupId = "";
    private String database = "";
    private int workThread = 20;
    private int defaultGateWayQueueSize = 500;
    private ICounterStrategy sleepStrategy = null;
    private ICounterStrategy threadCountStrategy = null;
    private EventDrivenThreadPoolWorker w = null;
    private MWCWorkerService service = new MWCWorkerService(this);

    public MWCWorker() {
        try {
            AEEPlatform.getInstance().installService(this.service);
            this.service.start();
        } catch (AEERuntimeException var2) {
            var2.printStackTrace();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public EventDrivenThreadPoolWorker getEventDrivenThreadPoolWorker() {
        return this.w;
    }

    public void pakcageEventGenenrator(EventDrivenWorker dw) {
        UnionBlockQueueEventGenerator gtor = new UnionBlockQueueEventGenerator();
        gtor.setName(this.groupId + "_GATE");
        gtor.setSleepStrategy(this.sleepStrategy);
        gtor.setQueueSize(this.defaultGateWayQueueSize);
        Map<String, MWCConfItem> confs = MWCConf.getInstance().getConf();
        Iterator i$ = confs.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry<String, MWCConfItem> e = (Map.Entry)i$.next();
            MWCConfItem item = (MWCConfItem)e.getValue();
            BlockLoaderEventGenerator bt = new BlockLoaderEventGenerator();
            bt.setName((String)e.getKey());
            TableDataEventLoader l = new TableDataEventLoader();
            l.setDatabase(item.getDatabase());
            l.setSelectSQL(item.getSelectSQL());
            l.setUpdateSQL(item.getUpdateSQL());
            bt.setLoader(l);
            if(NumberUtils.isNumber(item.getSleepStrategy())) {
                bt.setSleepStrategy(new StaticWorkCounterStrategy(Float.parseFloat(item.getSleepStrategy())));
            } else if(item.getSleepStrategy().startsWith("<sleepStrategy")) {
                String s;
                try {
                    Map<String, String> prop = XMLInputStreamParser.parseString(item.getSleepStrategy());
                    s = (String)prop.get("sleepStrategy");
                    Map<String, String> ms = MapTools.getSub(prop, "sleepStrategy");
                    ICounterStrategy ic = (ICounterStrategy) ObjectBuilder.build(ICounterStrategy.class, s, ms);
                    bt.setSleepStrategy(ic);
                } catch (Exception var13) {
                    s = "create sleepstrategt for " + (String)e.getKey() + ",get exception as " + ExceptionUtils.getExceptionStack(var13);
                    AEEExceptionProcessor.process(new Exception(s));
                }
            } else {
                bt.setSleepStrategy(this.sleepStrategy);
            }

            bt.setQueueSize(item.getQueueLength());
            gtor.addBlockLoaderEventGenerator((String)e.getKey(), bt, item.getQueueWeight());
        }

        dw.setEventGenerator(gtor);
    }

    @Override
    public Worker createWorker(String name) {
        MWCConf.getInstance().loadConf(this.database, this.groupId);
        EventDrivenThreadPoolWorker w = new EventDrivenThreadPoolWorker();
        w.setName("MWC_" + this.groupId);
        w.setConfig(this.getWorkerConfig());
        if(this.sleepStrategy == null) {
            this.sleepStrategy = new StaticWorkCounterStrategy(1);
        } else {
            w.setSleepStrategy(this.sleepStrategy);
        }

        if(this.threadCountStrategy != null) {
            w.setThreadCountStrategy(this.threadCountStrategy);
        } else {
            w.setCoreThreadCount(this.workThread);
            w.setMaxThreadCount(this.workThread);
        }

        this.pakcageEventGenenrator(w);
        MWCJob j = new MWCJob();
        w.setJob(j);
        this.w = w;
        return w;
    }

    public static void main(String[] args) throws Exception{
        MWCWorker mwcWorker = new MWCWorker();

        mwcWorker.database="cen1";
        mwcWorker.groupId="pf_actp_newactgtm";
//        EventDrivenWorker dw = new EventDrivenWorker();
//        mwcWorker.pakcageEventGenenrator(dw);
//        Map<String ,String> conf = new HashedMap();
//        conf.put("job","com.ailk.aee.acct.sms.TiOSmsAcctJob");
//        mwcWorker.setConfig(conf);
        Map<String, String> ms = new HashedMap();
        ms.put("work","pf_actp_newactgtm");
        ms.put("job","com.ailk.aee.acct.sms.TiOSmsAcctJob");
        Worker worker = (Worker)ObjectBuilder.build(Worker.class,"com.ailk.aee.app.mwci.MWCWorker",ms);
        worker.run();
//        mwcWorker.createWorker("pf_actp_newactgtm").run();

    }
}
