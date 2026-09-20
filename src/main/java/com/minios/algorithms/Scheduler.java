package com.minios.algorithms;

import com.minios.models.OSProcess;
import java.util.*;

public class Scheduler {

    public static List<OSProcess> runFCFS(List<OSProcess> processes) {
        List<OSProcess> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(OSProcess::getArrivalTime));
        
        int currentTime = 0;
        for (OSProcess p : sorted) {
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }
            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }
        return sorted;
    }

    public static List<OSProcess> runSJF(List<OSProcess> processes) {
        List<OSProcess> result = new ArrayList<>();
        List<OSProcess> pool = new ArrayList<>(processes);
        int currentTime = 0;
        
        while (!pool.isEmpty()) {
            final int t = currentTime;
            OSProcess shortest = pool.stream()
                    .filter(p -> p.getArrivalTime() <= t)
                    .min(Comparator.comparingInt(OSProcess::getBurstTime))
                    .orElse(null);
            
            if (shortest == null) {
                currentTime = pool.stream().mapToInt(OSProcess::getArrivalTime).min().orElse(currentTime);
                continue;
            }
            
            currentTime += shortest.getBurstTime();
            shortest.setCompletionTime(currentTime);
            shortest.setTurnaroundTime(shortest.getCompletionTime() - shortest.getArrivalTime());
            shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());
            
            result.add(shortest);
            pool.remove(shortest);
        }
        return result;
    }

    public static List<OSProcess> runRoundRobin(List<OSProcess> processes, int quantum) {
        Queue<OSProcess> queue = new LinkedList<>();
        List<OSProcess> result = new ArrayList<>(processes);
        Map<OSProcess, Integer> remainingTime = new HashMap<>();
        for (OSProcess p : processes) remainingTime.put(p, p.getBurstTime());
        
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        
        while (completed < n) {
            for (OSProcess p : processes) {
                if (p.getArrivalTime() <= currentTime && !queue.contains(p) && remainingTime.get(p) > 0) {
                    queue.add(p);
                }
            }
            
            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }
            
            OSProcess p = queue.poll();
            int timeSpent = Math.min(remainingTime.get(p), quantum);
            remainingTime.put(p, remainingTime.get(p) - timeSpent);
            currentTime += timeSpent;
            
            if (remainingTime.get(p) == 0) {
                p.setCompletionTime(currentTime);
                p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
                p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
                completed++;
            } else {
                // Check if new processes arrived during execution before re-adding
                for (OSProcess next : processes) {
                    if (next.getArrivalTime() <= currentTime && !queue.contains(next) && remainingTime.get(next) > 0 && next != p) {
                        queue.add(next);
                    }
                }
                queue.add(p);
            }
        }
        return result;
    }

    public static List<OSProcess> runPriority(List<OSProcess> processes) {
        List<OSProcess> result = new ArrayList<>();
        List<OSProcess> pool = new ArrayList<>(processes);
        int currentTime = 0;
        
        while (!pool.isEmpty()) {
            final int t = currentTime;
            OSProcess highest = pool.stream()
                    .filter(p -> p.getArrivalTime() <= t)
                    .min(Comparator.comparingInt(OSProcess::getPriority)) // Lower value = higher priority
                    .orElse(null);
            
            if (highest == null) {
                currentTime = pool.stream().mapToInt(OSProcess::getArrivalTime).min().orElse(currentTime);
                continue;
            }
            
            currentTime += highest.getBurstTime();
            highest.setCompletionTime(currentTime);
            highest.setTurnaroundTime(highest.getCompletionTime() - highest.getArrivalTime());
            highest.setWaitingTime(highest.getTurnaroundTime() - highest.getBurstTime());
            
            result.add(highest);
            pool.remove(highest);
        }
        return result;
    }
}
