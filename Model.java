package DQN;

public class Model {

    public void init() {
        // TODO: ネットワーク初期化
    }

    public int reward(Action a) {
        return 0;
    }

    public void updateQ(int r, Action a) {

    }

    public void updateS(Action a) {

    }

    private Tensor ReLU(Tensor input) {
        return input.apply_function((x) -> x > 0 ? x : 0);
    }
}
