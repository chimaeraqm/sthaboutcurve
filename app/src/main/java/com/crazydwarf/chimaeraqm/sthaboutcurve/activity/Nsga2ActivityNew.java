package com.crazydwarf.chimaeraqm.sthaboutcurve.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nsga2ActivityNew extends AppCompatActivity
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
    int index_of(Double a, ArrayList<Double> list)
    {
        int pos = list.indexOf(a);
        return pos;
    }

    //Function to find index of list
    int index_of(Integer a,ArrayList<Integer> list)
    {
        int pos = list.indexOf(a);
        return pos;
    }

    //Function get min of array;
    double getMin(ArrayList<Double> values)
    {
        double min = 0;
        if(values.size() > 0)
            min = values.get(0);
        if(values.size() > 1)
        {
            for(int i=1;i<values.size();i++)
            {
                double compare = values.get(i);
                if (compare < min)
                    min = compare;
            }
        }
        return min;
    }

    //Function get max of array;
    double getMax(ArrayList<Double> values)
    {
        double max = 0;
        if(values.size() > 0)
            max = values.get(0);
        if(values.size() > 1)
        {
            for(int i=1;i<values.size();i++)
            {
                double compare = values.get(i);
                if (compare < max)
                    max = compare;
            }
        }
        return max;
    }

    //Function to sort by values
    ArrayList<Integer> sortbyValues(ArrayList<Integer> list1,ArrayList<Double> values)
    {
        ArrayList<Integer> sorted_list = new ArrayList<Integer>();
        while(sorted_list.size() != list1.size())
        {
            double min = getMin(values);
            int index = index_of(min,values);
            if(index_of(index,list1) >= 0)
            {
                int index1 = index_of(getMin(values),values);
                sorted_list.add(index1);
            }
            int index2 = index_of(getMin(values),values);
            values.set(index2,Double.MAX_VALUE);
        }
        return sorted_list;
    }

    //Function to carry out NSGA-II's fast non dominated sort
    ArrayList fast_non_dominated_sort(ArrayList<Double> values1, ArrayList<Double> values2)
    {
        //sourcecode:
        //S=[[] for i in range(0,len(values1))] 新建数组S包含 values1.length行，每行元素为空null
        //S is Type of ArrayList<ArrayList<Integer>>
        ArrayList S = new ArrayList();

        //front为二维空数组
        //front is Type of ArrayList<ArrayList<Integer>>
        ArrayList front = new ArrayList();

        //新建数组n,rank并初始化
        ArrayList<Integer> n = new ArrayList<Integer>();
        ArrayList<Integer> rank = new ArrayList<Integer>();

        for(int p=0;p<values1.size();p++)
        {
            S.add(new ArrayList<Integer>());
            n.add(0);
            rank.add(0);
            for(int q=0;q<values1.size();q++)
            {
                if ((values1.get(p) >= values1.get(q) && values2.get(p) >= values2.get(q))
                        || (values1.get(p) >= values1.get(q) && values2.get(p) > values2.get(q))
                        || (values1.get(p) > values1.get(q) && values2.get(p) >= values2.get(q)))
                {
                    ArrayList<Integer> Sp = (ArrayList<Integer>) S.get(p);
                    if(index_of(q,Sp) < 0)
                    {
                        S.set(p,appendInt(Sp,q));
                    }
                }
                else if ((values1.get(q) > values1.get(p) && values2.get(q) > values2.get(p))
                        || (values1.get(q) >= values1.get(p) && values2.get(q) > values2.get(p))
                        || (values1.get(q) > values1.get(p) && values2.get(q) >= values2.get(p)))
                {
                    int np = n.get(p) + 1;
                    n.set(p,np);
                }
            }
            if (n.get(p)==0)
            {
                rank.set(p,0);
                ArrayList<Integer> front0 = new ArrayList<Integer>();
                if(front != null && front.size() > 0)
                    front0 = (ArrayList<Integer>)front.get(0);
                front0.add(p);
                if(front == null || front.size() == 0)
                    front.add(front0);
                else
                    front.set(0,front0);
            }
        }

        int i=0;
        if(front != null && front.size() > 0)
        {
            while(front.get(i) != null)
            {
                ArrayList<Integer> Q = new ArrayList<Integer>();
                for(int p : (ArrayList<Integer>) front.get(i))
                {
                    for(int q : (ArrayList<Integer>) S.get(p))
                    {
                        int nq = n.get(q) - 1;
                        n.set(q,nq);
                        if (n.get(q) == 0)
                        {
                            rank.set(q,i+1);
                            if(index_of(q,Q) < 0)
                            {
                                Q.add(q);
                            }
                        }
                    }
                }
                i++;
                front.add(Q);
                if(Q == null || Q.size() == 0)
                    break;
            }

            //删除最后为空的数组
            front.remove(front.size()-1);
        }
        return front;
    }

    //Function to calculate crowding distance
    ArrayList<Double> crowding_distance(ArrayList<Double> values1,ArrayList<Double> values2,ArrayList<Integer> front)
    {
        ArrayList<Double> distance = new ArrayList<Double>();
        for(int i=0;i<front.size();i++)
        {
            distance.add(0.);
        }
        ArrayList<Integer> sorted1 = sortbyValues(front,values1);
        ArrayList<Integer> sorted2 = sortbyValues(front,values2);
        distance.set(0,Double.MAX_VALUE);
        distance.set(front.size()-1,Double.MAX_VALUE);
        for(int k = 1;k < front.size()-1;k++)
        {
            double res = distance.get(k) + (values1.get(sorted1.get(k+1)) - values2.get(sorted1.get(k-1)))/(getMax(values1)-getMin(values1));
            distance.set(k,res);
        }
        for(int k = 1;k<front.size()-1;k++)
        {
            double res = distance.get(k) + (values1.get(sorted2.get(k+1)) - values2.get(sorted2.get(k-1)))/(getMax(values2)-getMin(values2));
            distance.set(k,res);
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
        ArrayList<Double> solution = new ArrayList<>();
        for(int i=0;i<pop_size;i++)
        {
            double new_value = min_x + (max_x - min_x)*Math.random();
            solution.add(new_value);
        }
        int gen_no = 0;
        while (gen_no < max_gen)
        {
            ArrayList<Double> function1_values = new ArrayList<Double>();
            ArrayList<Double> function2_values = new ArrayList<Double>();
            for(int i=0;i<pop_size;i++)
            {
                function1_values.add(function1(solution.get(i)));
                function2_values.add(function2(solution.get(i)));
            }
            ArrayList non_dominated_sorted_solution = fast_non_dominated_sort(function1_values,function2_values);
            System.out.println("The best front for Generation number "+ gen_no +" is");
            for(int valuez : (ArrayList<Integer>) non_dominated_sorted_solution.get(0))
            {
                System.out.println(Math.round(solution.get(valuez)));
            }
            System.out.println("\n");
            //crowding_distance_values is TYPE of ArrayList<ArrayList<Double>>
            ArrayList crowding_distance_values = new ArrayList();
            for(int i=0;i<non_dominated_sorted_solution.size();i++)
            {
                ArrayList<Double> crowding_distance_array = crowding_distance(function1_values,function2_values,(ArrayList<Integer>) non_dominated_sorted_solution.get(i));
                crowding_distance_values.add(crowding_distance_array);
            }
            ArrayList<Double> solution2 = (ArrayList<Double>) solution.clone();
            //Generating offsprings
            while (solution2.size() != pop_size*2)
            {
                int a1 = (int) (Math.random() * pop_size);
                int b1 = (int) (Math.random() * pop_size);
                double crossoverRes = crossover(solution.get(a1),solution.get(b1));
                solution2.add(crossoverRes);
            }

            ArrayList<Double> function1_values2 = new ArrayList<>();
            ArrayList<Double> function2_values2 = new ArrayList<>();
            for(int i=0;i<2*pop_size;i++)
            {
                function1_values2.add(function1(solution2.get(i)));
                function2_values2.add(function2(solution2.get(i)));
            }

            //non_dominated_sorted_solution2 is TYPE of ArrayList<ArrayList<Integer>>
            ArrayList non_dominated_sorted_solution2 = fast_non_dominated_sort(function1_values2,function2_values2);

            //crowding_distance_values2 is TYPE of ArrayList<ArrayList<Double>>
            ArrayList crowding_distance_values2 = new ArrayList();
            for(int i=0;i<non_dominated_sorted_solution2.size();i++)
            {
                ArrayList<Double> crowding_distance_array2 = crowding_distance(function1_values2,function2_values2,(ArrayList<Integer>) non_dominated_sorted_solution2.get(i));
                crowding_distance_values2.add(crowding_distance_array2);
            }

            ArrayList<Integer> new_solution = new ArrayList<>();
            for(int i=0;i<non_dominated_sorted_solution2.size();i++)
            {
                ArrayList<Integer> non_dominated_sorted_solution2_1 = new ArrayList<Integer>();
                ArrayList<Integer> non_dominated_sorted_solution2_i = (ArrayList<Integer>)non_dominated_sorted_solution2.get(i);
                for(int j=0;j<non_dominated_sorted_solution2_i.size();j++)
                {
                    Integer target = non_dominated_sorted_solution2_i.get(j);
                    int pos = non_dominated_sorted_solution2_i.indexOf(target);
                    non_dominated_sorted_solution2_1.add(pos);
                }

                ArrayList<Double> crowding_distance_values2_i = (ArrayList<Double>) crowding_distance_values2.get(i);
                ArrayList<Integer> front22 = sortbyValues(non_dominated_sorted_solution2_1,crowding_distance_values2_i);

                ArrayList<Integer> front = new ArrayList<Integer>();
                for(int j=0;j<non_dominated_sorted_solution2_i.size();j++)
                {
                    Integer target = non_dominated_sorted_solution2_i.get(front22.get(j));
                    int pos = non_dominated_sorted_solution2_i.indexOf(target);
                    front.add(pos);
                }

                //获得front的倒序数列reverseFront
                ArrayList<Integer> reverseFront = new ArrayList<Integer>();
                for(int j=0;j<front.size();j++)
                {
                    int target = front.get(j);
                    reverseFront.set(front.size()-j-1,target);
                }
                for(int value : front)
                {
                    new_solution.add(value);
                    if(new_solution.size() == pop_size)
                    {
                        break;
                    }
                }
                if (new_solution.size() == pop_size)
                {
                    break;
                }
            }
            for(int i : new_solution)
            {
                double res = solution2.get(i);
                solution.add(res);
            }
            gen_no ++;
        }
    }

    /**
     *
     * followed 4 are backup functions.
     */
    //Function to append int
    ArrayList<Integer> appendInt(ArrayList<Integer> oriList, int append)
    {
        ArrayList<Integer> newList = new ArrayList<Integer>();
        if(oriList != null)
        {
            newList = (ArrayList<Integer>) oriList.clone();
        }
        newList.add(append);
        return newList;
    }

    //Function to append int array
    //oriList is Type of ArrayList<ArrayList<Integer>>
    ArrayList appendInts(ArrayList oriList, ArrayList<Integer> appendList)
    {
        ArrayList newList = new ArrayList();
        if(oriList != null)
        {
            newList = (ArrayList) oriList.clone();
        }
        newList.add(appendList);
        return newList;
    }

    //Function to append double
    ArrayList<Double> appendDouble(ArrayList<Double> oriList, double append)
    {
        ArrayList<Double> newList = new ArrayList<Double>();
        if(oriList != null)
        {
            newList = (ArrayList<Double>) oriList.clone();
        }
        newList.add(append);
        return newList;
    }

    //Function to append double array
    //oriList is Type of ArrayList<ArrayList<Double>>
    ArrayList appendDoubles(ArrayList oriList, ArrayList<Double> appendList)
    {
        ArrayList newList = new ArrayList();
        if(oriList != null)
        {
            newList = (ArrayList) oriList.clone();
        }
        newList.add(appendList);
        return newList;
    }
}
