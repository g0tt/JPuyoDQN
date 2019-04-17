package DQN;

import java.util.function.Function;

public class Tensor {

    /**
     * 次元数
     * shape.lengthに等しい
     */
    private int dimension;

    /**
     * シェイプ
     */
    private int[] shape;

    /**
     * 生データ
     */
    private double[] data;

    // Construct ------------------------------------------------------------------------
    public Tensor(int[] shape, double[] init) {
        this.dimension = shape.length;
        this.shape = shape;
        int size = Tensor.getArraySize(shape);
        this.data = new double[size];
        System.arraycopy(init, 0, this.data, 0, size);
    }

    public Tensor(int[] shape) {
        this(shape, new double[Tensor.getArraySize(shape)]);
    }

    /**
     * 単位行列
     * @param size
     * @param dimension
     * @return
     */
    public static Tensor identity(int size, int dimension) {
        int dataLength = (int)Math.pow(size, dimension);
        double[] data = new double[dataLength];
        for (int i = 0; i < size; i++) {
            data[i*(size+1)] = 1;
        }
        int[] shape = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            shape[i] = size;
        }
        return new Tensor(shape, data);
    }

    /**
     * 複製
     * @return
     */
    public Tensor clone() {
        return new Tensor(this.shape.clone(), this.data.clone());
    }

    // Utility --------------------------------------------------------------------------
    /**
     * 生データ用の配列サイズを計算
     * @param shape
     * @return
     */
    private static int getArraySize(int[] shape) {
        int size = 1;
        for (int i = 0; i < shape.length; i++) {
            size *= shape[i];
        }
        return size;
    }

    /**
     * 行列の表示
     */
    public void print() {
        if (this.dimension == 2) {
            for (int i = 0; i < this.shape[0]; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < this.shape[1]; j++) {
                    if (j != 0) line.append(" ");
                    line.append(this.data[i*this.shape[1]+j]);
                }
                System.out.println(new String(line));
            }
        }
    }

    // Calculation ----------------------------------------------------------------------
    /**
     * 引数を後ろからかける積 とりあえず2次元まででいい気がする……？
     * @param other
     * @return
     */
    public Tensor times(Tensor other) {
        // サイズエラー
        if (this.dimension > 2 || other.dimension > 2) {
            return null;
        }
        if (this.dimension == 2 && other.dimension == 2) { // TODO: 次元拡張
            if (this.shape[1] != other.shape[0]) {
                return null;
            }

            int[] resultShape = new int[2];
            resultShape[0] = this.shape[0];
            resultShape[1] = other.shape[1];
            double[] resultData = new double[Tensor.getArraySize(resultShape)];

            for (int i = 0; i < resultShape[0]; i++) {
                for (int j = 0; j < resultShape[1]; j++) {
                    // TODO: i, jについて並列化可能
                    for (int k = 0; k < this.shape[1]; k++) {
                        resultData[i*resultShape[1]+j] += this.data[i*this.shape[1]+k] * other.data[k*other.shape[1]+j];
                    }
                }
            }
            return new Tensor(resultShape, resultData);
        } else {
            return null;
        }
    }

    /**
     * 全要素に関数を適用
     * @param fn
     * @return
     */
    public Tensor apply_function(Function<Double, Double> fn) {
        Tensor result = this.clone();
        for (int i = 0; i < this.data.length; i++) {
            // TODO: 並列
            result.data[i] = fn.apply(result.data[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        Tensor first = Tensor.identity(5, 2);
        Tensor second = Tensor.identity(5, 2);
        Tensor res = first.times(second);
        res.print();
        res.data[0] = -1;
        res.apply_function((i) -> i * 3.0).print();
    }
}
