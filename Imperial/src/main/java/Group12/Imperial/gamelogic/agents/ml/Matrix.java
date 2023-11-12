package Group12.Imperial.gamelogic.agents.ml;

import java.util.ArrayList;
import java.util.List;

public class Matrix {

    public double[][] data;
    public int rows;
    public int cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public void fillRandom() {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] = Math.random()*2-1;
            }
        }
    }

    public void fillOnes() {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] = 1.0;
            }
        }
    }

    public void add(double x) {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                this.data[i][j] += x;
            }
        }
    }

    public void add(Matrix other) {
        if(cols != other.cols || rows != other.rows) {
            System.out.println("Shape Mismatch");
            return;
        }
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                this.data[i][j] += other.data[i][j];
            }
        }
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix temp = new Matrix(a.rows, a.cols);
        for(int i = 0; i < a.rows; i++) {
            for(int j = 0; j < a.cols; j++) {
                temp.data[i][j] = a.data[i][j] - b.data[i][j];
            }
        }
        return temp;
    }

    public static Matrix transpose(Matrix a) {
        Matrix temp = new Matrix(a.cols, a.rows);
        for(int i = 0; i < a.rows; i++) {
            for(int j = 0; j < a.cols; j++) {
                temp.data[j][i] = a.data[i][j];
            }
        }
        return temp;
    }

    public static Matrix multiply(Matrix a, Matrix b, boolean multiThread) {
        Matrix temp = new Matrix(a.rows, b.cols);
        if(multiThread) {
            ParallelThreadsCreator.multiply(a, b, temp);
        } else {
            for(int i = 0; i < temp.rows; i++) {
                for(int j = 0; j < temp.cols; j++) {
                    double sum = 0;
                    for(int k = 0; k < a.cols; k++) {
                        sum += a.data[i][k] * b.data[k][j];
                    }
                    temp.data[i][j] = sum;
                }
            }
        }
        
        
        return temp;
    }
    
    public void multiply(Matrix a) {
        for(int i = 0; i < a.rows; i++) {
            for(int j = 0; j < a.cols; j++) {
                this.data[i][j] *= a.data[i][j];
            }
        }
        
    }
    
    public void multiply(double a) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                this.data[i][j] *= a;
            }
        }
        
    }

    public void applyFunction(Function f) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                this.data[i][j] = f.apply(this.data[i][j]);
            }
        }
    }

    public static Matrix fromArray(double[] x) {
        Matrix temp = new Matrix(x.length, 1);
        for(int i = 0; i < x.length; i++)
            temp.data[i][0] = x[i];
        return temp;
    }
    
    public ArrayList<Double> toArrayList() {
        ArrayList<Double> temp = new ArrayList<>()  ;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                temp.add(data[i][j]);
            }
        }
        return temp;
   }

    
}

class ParallelThreadsCreator {

    public static void multiply(Matrix matrix1, Matrix matrix2, Matrix result) {
        List<Thread> threads = new ArrayList<>();
        int rows1 = matrix1.rows;
        for (int i = 0; i < rows1; i++) {
            RowMultiplyWorker task = new RowMultiplyWorker(result, matrix1, matrix2, i);
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
            if (threads.size() % 10 == 0) {
                waitForThreads(threads);
            }
        }
    }

    private static void waitForThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threads.clear();
    }
}

class WorkerThread extends Thread {
    private int row;
    private int col;
    private int[][] A;
    private int[][] B;
    private int[][] C;

    public WorkerThread(int row, int col, int[][] A, int[][] B, int[][] C) {
        this.row = row;
        this.col = col;
        this.A = A;
        this.B = B;
        this.C = C;
    }

    public void run() {
        C[row][col] = (A[row][0] * B[0][col]) + (A[row][1] * B[1][col]);
    }
}

class RowMultiplyWorker implements Runnable {

    private final Matrix result;
    private Matrix matrix1;
    private Matrix matrix2;
    private final int row;

    public RowMultiplyWorker(Matrix result, Matrix matrix1, Matrix matrix2, int row) {
        this.result = result;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.row = row;
    }

    @Override
    public void run() {
        for (int i = 0; i < matrix2.data[0].length; i++) {
            result.data[row][i] = 0;
            for (int j = 0; j < matrix1.data[row].length; j++) {
                result.data[row][i] += matrix1.data[row][j] * matrix2.data[j][i];
            }

        }
    }
}
