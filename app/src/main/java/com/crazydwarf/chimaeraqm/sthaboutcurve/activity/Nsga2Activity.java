package com.crazydwarf.chimaeraqm.sthaboutcurve.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.math.MathUtils;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Nsga2Activity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calculation();
    }

    //First function to optimize
    double function1(double x)
    {
        double res = -Math.pow(x,2);
        return res;
    }

    //Second function to optimize
    double function2(double x)
    {
        double res = -Math.pow(x-2,2);
        return res;
    }

    //Function to find index of list
    int index_of(double a, Double list[])
    {
        if(list == null)
            return -1;
        for(int i=0;i<list.length;i++)
        {
            double listi = list[i];
            if(listi == a)
                return i;
        }
        return -1;
    }

    //Function to find index of list
    int index_of(int a,Integer[] list)
    {
        if(list == null)
            return -1;
        for(int i=0;i<list.length;i++)
        {
            int listi = list[i];
            if(listi == a)
                return i;
        }
        return -1;
    }

    //Function get min of array;
    double getMin(Double[] values)
    {
        double min = 0;
        if(values.length > 0)
            min = values[0];
        if(values.length > 1)
        {
            for(int i=1;i<values.length;i++)
            {
                double compare = values[i];
                if (compare < min)
                    min = compare;
            }
        }
        return min;
    }

    //Function get max of array;
    double getMax(Double[] values)
    {
        double max = 0;
        if(values.length > 0)
            max = values[0];
        if(values.length > 1)
        {
            for(int i=1;i<values.length;i++)
            {
                double compare = values[i];
                if (compare < max)
                    max = compare;
            }
        }
        return max;
    }

    //Function to append int
    Integer[] appendInt(Integer[] oriList, int append)
    {
        List<Integer> newList = Arrays.asList(oriList);
        newList.add(append);
        Integer[] resList = new Integer[newList.size()];
        newList.toArray(resList);
        return resList;
    }

    //Function to append int array
    Integer[][] appendInts(Integer[][] oriList, Integer[] appendList)
    {
        List<Integer[]> newList = Arrays.asList(oriList);
        newList.add(appendList);
        Integer[][] resList = new Integer[newList.size()][];
        newList.toArray(resList);
        return resList;
    }

    //Function to append double
    Double[] appendDouble(Double[] oriList, double append)
    {
        List<Double> newList = Arrays.asList(oriList);
        newList.add(append);
        Double[] resList = new Double[newList.size()];
        newList.toArray(resList);
        return resList;
    }

    //Function to append double array
    Double[][] appendDoubles(Double[][] oriList, Double[] appendList)
    {
        List<Double[]> newList = Arrays.asList(oriList);
        newList.add(appendList);
        Double[][] resList = new Double[newList.size()][];
        newList.toArray(resList);
        return resList;
    }

    //Function to sort by values
    Integer[] sortbyValues(Integer list1[],Double values[])
    {
        Integer[] sorted_list = null;
        while(sorted_list.length != list1.length)
        {
            double min = getMin(values);
            int index = index_of(min,values);
            if(Arrays.binarySearch(list1,index) >= 0)
            {
                int index1 = index_of(getMin(values),values);
                appendInt(sorted_list,index1);
            }
            int index2 = index_of(getMin(values),values);
            values[index2]  = Double.MAX_VALUE;
        }
        return sorted_list;
    }

    //Function to carry out NSGA-II's fast non dominated sort
    Integer[][] fast_non_dominated_sort(Double[] values1, Double[] values2)
    {
        //sourcecode:
        //S=[[] for i in range(0,len(values1))] 新建数组S包含 values1.length行，每行元素为空null
        Integer[][] S = new Integer[values1.length][];

        //front为二维空数组
        Integer[][] front = null;

        //新建数组n,rank并初始化
        int n[] = new int[values1.length];
        int rank[] = new int[values1.length];
        for(int i=0;i<values1.length;i++)
        {
            n[i] = 0;
            rank[i] = 0;
        }

        for(int p=0;p<values1.length;p++)
        {
            S[p] = null;
            n[p] = 0;
            for(int q=0;q<values1.length;q++)
            {
                if ((values1[p] > values1[q] && values2[p] > values2[q])
                        || (values1[p] >= values1[q] && values2[p] > values2[q])
                        || (values1[p] > values1[q] && values2[p] >= values2[q]))
                {
                    if(index_of(q,S[p]) < 0)
                    {
                        appendInt(S[p],q);
                    }
                }
                else if ((values1[q] > values1[p] && values2[q] > values2[p])
                        || (values1[q] >= values1[p] && values2[q] > values2[p])
                        || (values1[q] > values1[p] && values2[q] >= values2[p]))
                {
                    n[p] = n[p] + 1;
                }
            }
            if (n[p]==0)
            {
                rank[p] = 0;
                if(index_of(p,front[0]) < 0)
                {
                    appendInt(front[0],p);
                }
            }
        }

        int i=0;
        while(front[i] != null)
        {
            Integer Q[] = null;
            for(int p : front[i])
            {
                for(int q : S[p])
                {
                    n[q] = n[q] - 1;
                    if (n[q] == 0)
                    {
                        rank[q] = i+1;
                        if(index_of(q,Q) < 0)
                        {
                            appendInt(Q,q);
                        }
                    }
                }
            }
            i++;
            appendInts(front,Q);
        }
        front = Arrays.copyOfRange(front,0,front.length-2);
        return front;
    }


    //Function to calculate crowding distance
    Double[] crowding_distance(Double values1[],Double values2[],Integer front[])
    {
        Double[] distance = new Double[front.length];
        for(int i=0;i<distance.length;i++)
        {
            distance[i] = 0.;
        }
        Integer[] sorted1 = sortbyValues(front,values1);
        Integer[] sorted2 = sortbyValues(front,values2);
        distance[0] = Double.MAX_VALUE;
        distance[front.length-1] = Double.MAX_VALUE;
        for(int k = 1;k<front.length-1;k++)
        {
            distance[k] = distance[k] + (values1[sorted1[k+1]] - values2[sorted1[k-1]])/(getMax(values1)-getMin(values1));
        }
        for(int k = 1;k<front.length-1;k++)
        {
            distance[k] = distance[k] + (values1[sorted2[k+1]] - values2[sorted2[k-1]])/(getMax(values2)-getMin(values2));
        }
        return distance;
    }

    //Function to carry out the crossover
    double crossover(double a,double b)
    {
        double r = Math.random();
        if(r > 0.5)
        {
            double res = mutation((a + b)/2);
            return res;
        }
        else
        {
            double res = mutation((a - b)/2);
            return res;
        }
    }

    //Function to carry out the mutation operator
    double mutation(double solution)
    {
        double mutation_prob = Math.random();
        if(mutation_prob < 1)
        {
            solution = min_x + (max_x-min_x) * Math.random();
        }
        return solution;
    }

    int pop_size = 20;
    int max_gen = 921;
    double min_x = -55;
    double max_x = 55;

    void Calculation()
    {
        Double[] solution = new Double[pop_size];
        for(int i=0;i<pop_size;i++)
        {
            double new_value = min_x + (max_x - min_x)*Math.random();
            solution[i] = new_value;
        }
        int gen_no = 0;
        while (gen_no < max_gen)
        {
            Double function1_values[] = new Double[pop_size];
            Double function2_values[] = new Double[pop_size];
            for(int i=0;i<pop_size;i++)
            {
                function1_values[i] = function1(solution[i]);
                function2_values[i] = function2(solution[i]);
            }
            Integer[][] non_dominated_sorted_solution = fast_non_dominated_sort(function1_values,function2_values);
            System.out.println("The best front for Generation number "+ gen_no +" is");
            for(int valuez : non_dominated_sorted_solution[0])
            {
                System.out.println(Math.round(solution[valuez]));
            }
            System.out.println("\n");
            Double[][] crowding_distance_values = new Double[non_dominated_sorted_solution.length][];
            for(int i=0;i<non_dominated_sorted_solution.length;i++)
            {
                Double[] crowding_distance_array = crowding_distance(function1_values,function2_values,non_dominated_sorted_solution[i]);
                crowding_distance_values = Arrays.copyOf(crowding_distance_values,crowding_distance_values.length+crowding_distance_array.length);
                appendDoubles(crowding_distance_values,crowding_distance_array);
            }
            Double[] solution2 = solution.clone();
            //Generating offsprings
            while (solution2.length != pop_size*2)
            {
                int a1 = (int) (Math.random() * pop_size);
                int b1 = (int) (Math.random() * pop_size);
                double crossoverRes = crossover(solution[a1],solution[b1]);
                appendDouble(solution2,crossoverRes);
            }

            Double[] function1_values2 = new Double[2*pop_size];
            Double[] function2_values2 = new Double[2*pop_size];
            for(int i=0;i<2*pop_size;i++)
            {
                function1_values2[i] = function1(solution2[i]);
                function2_values2[i] = function2(solution2[i]);
            }
            Integer[][] non_dominated_sorted_solution2 = fast_non_dominated_sort(function1_values2,function2_values2);
            Double[][] crowding_distance_values2 = new Double[non_dominated_sorted_solution2.length][];
            for(int i=0;i<non_dominated_sorted_solution2.length;i++)
            {
                Double[] crowding_distance_array2 = crowding_distance(function1_values2,function2_values2,non_dominated_sorted_solution2[i]);
                crowding_distance_values2 = Arrays.copyOf(crowding_distance_values2,crowding_distance_values2.length+crowding_distance_array2.length);
                appendDoubles(crowding_distance_values2,crowding_distance_array2);
            }
            Integer[] new_solution = null;
            for(int i=0;i<non_dominated_sorted_solution2.length;i++)
            {
                Integer[] non_dominated_sorted_solution2_1 = new Integer[non_dominated_sorted_solution2[i].length];
                for(int j=0;j<non_dominated_sorted_solution2[i].length;j++)
                {
                    non_dominated_sorted_solution2_1[j] = index_of(non_dominated_sorted_solution2[i][j],non_dominated_sorted_solution2[i]);
                }
                Integer[] front22 = sortbyValues(non_dominated_sorted_solution2_1,crowding_distance_values2[i]);
                int[] front = new int[non_dominated_sorted_solution2[i].length];
                for(int j=0;j<non_dominated_sorted_solution2[i].length;j++)
                {
                    front[j] = index_of(non_dominated_sorted_solution2[i][front22[j]],non_dominated_sorted_solution2[i]);
                }

                double[] reverseFront = new double[front.length];
                for(int j=0;j<front.length;j++)
                {
                    reverseFront[front.length-j-1] = front[j];
                }
                for(int value : front)
                {
                    appendInt(new_solution,value);
                    if(new_solution.length == pop_size)
                    {
                        break;
                    }
                }
                if (new_solution.length == pop_size)
                {
                    break;
                }
            }
            for(int i : new_solution)
            {
                appendDouble(solution,solution2[i]);
            }
            gen_no ++;
        }
    }
}
