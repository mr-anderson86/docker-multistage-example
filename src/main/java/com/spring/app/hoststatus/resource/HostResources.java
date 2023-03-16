package com.spring.app.hoststatus.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.lang.management.ManagementFactory;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

@RestController
@RequestMapping("/")
public class HostResources {
    private String runCommand(String cmd) throws IOException {
        String command[] =  {"/bin/sh", "-c", cmd};
        Process uptimeProc = Runtime.getRuntime().exec(command);
        BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        return in.readLine();
    }

    private double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }

    @GetMapping
    public String hello() {
        String hostName="", osName="", upTime="", loadAvg="", totalMem="", usedMem="", freeMem="", cpuUsage="", diskSize="", diskUsed="", diskAvailable="";
        String newLine = "</br>";

        try {
            double totalMemMB = Double.parseDouble(runCommand("free -m | grep Mem | awk '{print $2}'"));
            double usedMemMB = Double.parseDouble(runCommand("free -m | grep Mem | awk '{print $3}'"));
            double freeMemMB = Double.parseDouble(runCommand("free -m | grep Mem | awk '{print $4}'"));
            double totalMemGB = totalMemMB / 1024;
            double usedMemGB = usedMemMB / 1024;
            double freeMemGB = freeMemMB / 1024;

            hostName = "Hostname: " + InetAddress.getLocalHost().getHostName() + newLine;
            osName = "Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + newLine ;
            upTime = "Uptime: " + runCommand("uptime | grep -oE 'up.*' | cut -d',' -f1 | sed 's/up //g'") + newLine;
            loadAvg = "Load AVG: " + runCommand("uptime | awk '{print $(NF-2),$(NF-1), $NF}'") + newLine;

            totalMem = "Total memory: " + String.format("%.2f", totalMemGB) + "GB" + newLine;
            usedMem = "Used memory: " + String.format("%.2f", usedMemGB) + "GB" + newLine;
            freeMem = "Free memory: " + String.format("%.2f", freeMemGB) + "GB" + newLine;

            double cpu = getProcessCpuLoad();
            cpuUsage = "CPU Usage: " + cpu +"%" + newLine;

            diskSize = "Disk size:" + runCommand("df -h . | tail -1 | awk '{print $2}'") + newLine;
            diskUsed = "Disk used:" + runCommand("df -h . | tail -1 | awk '{print $3}'") + newLine;
            diskAvailable = "Disk available:" + runCommand("df -h . | tail -1 | awk '{print $4}'") + newLine;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mainPage = "<h1>Host Activity Monitor</h1>";

        mainPage = mainPage + "<b>General</b>" + newLine;
        mainPage = mainPage + hostName + osName + upTime + loadAvg + newLine;
        mainPage = mainPage + "<b>CPU</b>" + newLine + cpuUsage + newLine;
        mainPage = mainPage + "<b>Memory</b>" + newLine;
        mainPage = mainPage + totalMem + usedMem + freeMem + newLine;
        mainPage = mainPage + "<b>Disk</b>" + newLine;
        mainPage = mainPage + diskSize + diskUsed + diskAvailable;
         
        return mainPage;
    }

}
