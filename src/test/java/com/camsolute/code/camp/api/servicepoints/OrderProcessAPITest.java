package com.camsolute.code.camp.api.servicepoints;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.camsolute.code.camp.lib.dao.rest.ProcessControlRest;
import com.camsolute.code.camp.lib.models.CampInstanceDao;
import com.camsolute.code.camp.lib.models.ModelDao;
import com.camsolute.code.camp.lib.models.order.Order;
import com.camsolute.code.camp.lib.models.order.OrderDao;
import com.camsolute.code.camp.lib.models.order.OrderPositionDao;
import com.camsolute.code.camp.lib.models.order.OrderRest;
import com.camsolute.code.camp.lib.models.process.OrderProcess;
//import com.camsolute.code.camp.lib.models.process.OrderProcess.OrderAction;
import com.camsolute.code.camp.lib.models.process.ProcessDao;
import com.camsolute.code.camp.lib.models.process.ProcessList;
import com.camsolute.code.camp.lib.models.process.Process;
import com.camsolute.code.camp.lib.models.process.Process.ProcessType;
import com.camsolute.code.camp.lib.models.product.ProductDao;
import com.camsolute.code.camp.lib.models.rest.Request.Principal;
import com.camsolute.code.camp.lib.models.rest.Task;
import com.camsolute.code.camp.lib.models.rest.VariableValue;
import com.camsolute.code.camp.lib.models.rest.VariableValue.VariableValueType;
import com.camsolute.code.camp.lib.models.rest.Variables;
import com.camsolute.code.camp.lib.utilities.Util;

public class OrderProcessAPITest {
	
	public static int testCases = 41;
	
	private static final Logger LOG = LogManager.getLogger(OrderProcessAPITest.class);
	private static String fmt = "[%15s] [%s]";
	
  private static int port = 8091;

  private static Tomcat tomcat;

  private static String serverUrl = "http://localhost:8091/camp.api/camp";

	@BeforeClass
 	public static void setUpBeforeClass() throws Exception {
		OrderDao.instance().createTable(false);
		OrderPositionDao.instance().createTable(false);
		ProductDao.instance().createTable(false);
		ModelDao.instance().createTable(false);
		ProcessDao.instance().createTable(false);
		CampInstanceDao.instance().createTable(false);
		OrderDao.instance().clearTables(false);
		OrderPositionDao.instance().clearTables(false);
		ProductDao.instance().clearTables(false);
		ModelDao.instance().clearTables(false);
		ProcessDao.instance().clearTables(false);
		CampInstanceDao.instance().clearTables(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testProcessWalk() {
		boolean log = true;
		boolean tlog = false;
		int test = 1;
		int count = 0;
		int covered = 1;
		Class<OrderProcessAPITest> TC = OrderProcessAPITest.class;
		long startTime = System.currentTimeMillis();
		String _f = null;
		String msg = null;
		if(log && !Util._IN_PRODUCTION) {
			_f = "[testCreate]";
			msg = "====[ TEST["+test+"] START ]: OrderAPITest.testProcessWalk test case Walk through the order process ]====";LOG.info(String.format(fmt,(_f+">>>>>>>>>").toUpperCase(),msg));
		}		
		String businessId = "012345678k";
		String businessKey = "com.camsolute.code.create.order";
		String date = Util.Time.timeStampString();
		String byDate = Util.Time.datetime(Util.Time.timestamp(Util.Time.nowPlus(100, Util.Time.formatDateTime)));
		String group = "com.camsolute.code.PW";
		String version = "1.0";
		
		// CREATE ORDER
		Order o = OrderRest.instance().create(serverUrl, businessId, businessKey, date, byDate, group, version, tlog);
	
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Order.id() - after persistance the order id must not be 0 - EXPECT[id!=0] RESULT['"+o.id()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(o.id()!=0);
		covered++;
		
		// START PROCESS
		//we start the Order Process by starting the core process: customer order process (DEFAULT_CUSTOMER_ORDER_PROCESS_KEY)
		Process<?> cop = ProcessControlRest.instance().startProcess(serverUrl,OrderProcess.DEFAULT_CUSTOMER_ORDER_PROCESS_KEY, o, Principal.Customer, tlog);
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Process - after starting the customer order process the cop process variable should not be null - EXPECT['cop != null'] RESULT['"+(cop!=null)+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		
		o.processes().add(cop);

		// SUBMIT ORDER 
		//update the Order status to SUBMITTED
		o.setStatus(Order.Status.SUBMITTED);
		//Starting the "Order Process"
		ProcessControlRest.instance().completeTask(cop.instanceId(),Principal.Order.name(), o, tlog);
		if(log && !Util._IN_PRODUCTION){msg = "----[cop.type() == "+cop.type().name()+"]----";LOG.info(String.format(fmt, _f,msg));}
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Submit order EXPECT['cop != null'] RESULT['"+(cop!=null)+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertNotNull(cop);
		covered++;
		
		o = OrderRest.instance().loadByBusinessId(o.onlyBusinessId(), tlog);
		ProcessList pl = OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog);
		for(Process<?> p:pl){
			if(log && !Util._IN_PRODUCTION){msg = "----[process.type() == '"+p.type().name()+"']----";LOG.info(String.format(fmt, _f,msg));}
		}
		o.setProcesses(pl);
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Order.Status EXPECT['"+Order.Status.SUBMITTED.name()+"'] RESULT['"+o.status().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(Order.Status.SUBMITTED.name().equals(o.status().name()));
		covered++;

		// RELEASE ORDER
		o.setStatus(Order.Status.PRODUCTION);
		for(Process<?> p:o.processes()){
			if(log && !Util._IN_PRODUCTION){msg = "----[process.type() == '"+p.type().name()+"']----";LOG.info(String.format(fmt, _f,msg));}
		}
		
		Process<?> comp = OrderProcess.OrderAction.RELEASE_ORDER.process(o);
		if(log && !Util._IN_PRODUCTION){msg = "----[comp.type() == "+comp.type().name()+"]----";LOG.info(String.format(fmt, _f,msg));}
		ProcessControlRest.instance().completeTask(comp.instanceId(),Principal.Order.name(), o, tlog);
		pl = OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog);
		o.setProcesses(pl);
		for(Process<?> p:pl) {
			if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
			msg = "====[TEST--["+test+"."+count+++"]: Release process EXPECT[Process.type() == '"+ProcessType.customer_order_process.name()+"||"+ProcessType.production_process.name()+"'] "
					+ "RESULT['"+p.type().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
			assertTrue(comp.type().name().equals(ProcessType.customer_order_process.name())||
					comp.type().name().equals(ProcessType.production_process.name()));
			covered++;
		}
		o = OrderRest.instance().loadByBusinessId(o.onlyBusinessId(), tlog);
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Order.Status EXPECT['"+Order.Status.PRODUCTION.name()+"'] RESULT['"+o.status().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(Order.Status.PRODUCTION.name().equals(o.status().name()));
		covered++;

		// SHIP ORDER		
		o.setStatus(Order.Status.SHIPPING);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		Process<?> pmp = OrderProcess.OrderAction.SHIP_ORDER.process(o);
		ProcessControlRest.instance().completeTask(pmp.instanceId(),Principal.Order.name(), o, tlog);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Release process EXPECT[Process.type() == '"+ProcessType.customer_order_process.name()+"'] RESULT['"+o.processes().get(0).type().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(o.processes().get(0).type().name().equals(ProcessType.customer_order_process.name()));
		covered++;
		
		o = OrderRest.instance().loadByBusinessId(o.onlyBusinessId(), tlog);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Order.Status EXPECT['"+Order.Status.SHIPPING.name()+"'] RESULT['"+o.status().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(Order.Status.SHIPPING.name().equals(o.status().name()));
		covered++;
		
		// FULFILL ORDER		
		o.setStatus(Order.Status.FULFILLED);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		cop = OrderProcess.OrderAction.ACKNOWLEDGE_DELIVERY.process(o);
		ProcessControlRest.instance().completeTask(cop.instanceId(),Principal.Order.name(), o, tlog);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Release process EXPECT[Order.pocesses().size() == '0'] RESULT['"+o.processes().size()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(o.processes().size()==0);
		covered++;
		
		o = OrderRest.instance().loadByBusinessId(o.onlyBusinessId(), tlog);
		o.setProcesses(OrderRest.instance().loadProcessReferences(o.onlyBusinessId(), tlog));
		if(log && !Util._IN_PRODUCTION){ String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
		msg = "====[TEST--["+test+"."+count+++"]: Order.Status EXPECT['"+Order.Status.FULFILLED.name()+"'] RESULT['"+o.status().name()+"']--]====";LOG.trace(String.format(fmt,_f.toUpperCase(),msg+coverage+time));}
		assertTrue(Order.Status.FULFILLED.name().equals(o.status().name()));
		covered++;		
		
		if(log && !Util._IN_PRODUCTION) {
			String coverage = "[Covered:"+Util.Test.coverage(covered,TC)+"%]=";
			String time = "[ExecutionTime:"+(System.currentTimeMillis()-startTime)+")]====";
			msg = "====[--[TEST["+test+++"] END ]: testCreate testcase completed]=";LOG.info(String.format(fmt,("<<<<<<<<<"+_f).toUpperCase(),msg+coverage+time));
		}
	}

}
