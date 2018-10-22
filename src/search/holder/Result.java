package search.holder;

/**
 * @author Yukio [GodWorld]
 * @version 22.10.2018 2:19
 */
public class Result {
	private double frequency;

	public Result(double frequency) {
		this.frequency = frequency;
	}

	public double getFrequency() {
		return frequency;
	}

	public void addFrequency(double frequency) {
		this.frequency += frequency;
	}
}
