 package storm.starter;

 import backtype.storm.Config;
 import backtype.storm.StormSubmitter;
 import backtype.storm.topology.TopologyBuilder;
 import storm.starter.bolt.FilterBolt;
 import storm.starter.bolt.TestBolt;
 import storm.starter.bolt.TransformBolt;
 import storm.starter.spout.RandomLogSpout;

 public class ProcessingTopology_LessSLO {
     public static void main(String[] args) throws Exception {
         //int numBolt = 3;
         int paralellism = 8;

         TopologyBuilder builder = new TopologyBuilder();

         builder.setSpout("spout_head", new RandomLogSpout(), paralellism + 2).setNumTasks(20);// +2 by Faria

         builder.setBolt("bolt_transform", new TransformBolt(), paralellism+2).shuffleGrouping("spout_head").setNumTasks(20);
         builder.setBolt("bolt_filter", new FilterBolt(), paralellism + 2).shuffleGrouping("bolt_transform").setNumTasks(20);
         builder.setBolt("bolt_join1", new TestBolt(), paralellism+2).shuffleGrouping("bolt_filter").setNumTasks(20);
         builder.setBolt("bolt_output_1", new TestBolt(),paralellism+2).shuffleGrouping("bolt_join1").setNumTasks(20);
         builder.setBolt("bolt_join2", new TransformBolt(), paralellism+2).shuffleGrouping("bolt_transform").setNumTasks(20);
         builder.setBolt("bolt_normalize", new TestBolt(),paralellism+2).shuffleGrouping("bolt_join2").setNumTasks(20);
         builder.setBolt("bolt_output_2", new TestBolt(),paralellism+2).shuffleGrouping("bolt_normalize").setNumTasks(20);

         Config conf = new Config();
         conf.setTopologySlo(0.8);
         conf.setDebug(true);

         conf.setNumAckers(0);

         conf.setNumWorkers(5);

         StormSubmitter.submitTopologyWithProgressBar(args[0], conf,
                 builder.createTopology());

     }

 }
