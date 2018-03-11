package com.vero.compiler.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class GramToExpr {

	/* �������ʽ */
	private ArrayList<String> input = new ArrayList<String>();
	/* �״μ����� */
	private ArrayList<String> result = new ArrayList<String>();
	/* ���ռ����� */
	private String finalResult = "";

	/**
	 * �������ʽ
	 */
	public void addInput(String str) {
		this.input.add(str);
	}

	/**
	 * ��ȡ�������ʽ����
	 */
	public int getInputSize() {
		return this.input.size();
	}

	/**
	 * �������
	 */
	public void clear() {
		this.input.clear();
		this.result.clear();
		;
		this.finalResult = "";
	}

	/**
	 * ��ȡ����ʽ��
	 * 
	 * @param str
	 * @return
	 */
	private String getLeft(String str) {
		String[] split = str.split("=");
		return split[0];
	}

	/**
	 * ��ȡ����ʽ�Ҳ�
	 * 
	 * @param str
	 * @return
	 */
	private String getRight(String str) {
		String[] split = str.split("=");
		return split[1];
	}

	/**
	 * ������ʽ�Ƿ�����ó˷�������չ���������ԣ�����չ����Ĳ���ʽ(ֻ����A=(a)*(b+c)��ʽ)
	 */
	private String distribute(String str) {
		String result = "";
		Stack<HashMap<String, Integer>> stack = new Stack<HashMap<String, Integer>>();
		HashMap<String, Integer> map = new HashMap<String, Integer>(); // ��¼��Ϣ��ֵ��
		HashMap<String, Integer> tempmap = new HashMap<String, Integer>();
		int start = -1, end = -1; // ��¼ÿ������ƥ��ɹ��Ŀ�ʼ�ͽ���λ��
		String extract = ""; // ÿ����ȡ���������Ŷ��е��ַ���
		String commonfactor = ""; // ��Ź���ʽ
		String[] split;
		String left = this.getLeft(str);
		String right = this.getRight(str);

		for (int i = 0; i < right.length(); i++) { // ����ƥ�䣬��¼�ַ�λ��
			map = new HashMap<String, Integer>();
			map.put(right.charAt(i) + "", i);
			if (right.charAt(i) == '(') { // �����Ž�ջ
				stack.push(map);
				// System.err.println("��ջ��(" + map.get("("));
			}
			if (right.charAt(i) == ')') { // �����Ž�ջ
				tempmap = new HashMap<String, Integer>();
				tempmap = (HashMap<String, Integer>) stack.peek();
				stack.pop();
				// System.err.println("ƥ�䣺)" + i);
				start = tempmap.get("(");
				end = i;
				extract = right.substring(start + 1, end);
				// System.err.println("extract:" + extract);
				if (extract.contains("+")) { // ����мӺţ�˵������ʽ�ֽ�
					split = extract.split("[+]");
					commonfactor = right.substring(0, start);
					for (int j = 0; j < split.length; j++) {
						if (j == 0) {
							result += commonfactor + split[j];
						} else {
							result += "+" + commonfactor + split[j];
						}
					}
					result = left + "=" + result
							+ right.substring(end + 1, right.length());
					return result;
				}
			}
		}
		return str;
	}

	/**
	 * �������������Բ���ʽ�������Լ�����ֱ�Ӽ��㣬������������ result
	 */
	public void analyzeOne(String str) {
		str = this.distribute(str); // ����ʽ�ֽ�
		boolean isok = false;
		String result_a = ""; // X=aX+b��a��
		String result_b = ""; // X=aX+b��b��
		String tempstr = ""; // ��Ŵ��滻���Ӵ�
		String[] split_1 = str.split("=");
		String left = split_1[0];
		String right = split_1[1];
		// System.out.println("left:" + left);
		// System.out.println("right:" + right);
		String[] split_2 = right.split("[+]");

		for (int i = 0; i < split_2.length; i++) {// ������ʽ�Ƿ����X=aX+b����ʽ
			if (split_2[i].contains(left)) {
				isok = true;
				result_a = split_2[i].substring(0, split_2[i].length() - 1);
				if (i == 0) { // ��һ�����ʽ���ұߴ��Ӻ�
					tempstr = split_2[i] + "+";
				} else if (i == split_2.length - 1
						|| (i > 0 && i < split_2.length - 1 && split_2.length > 1)) { // ���һ�����м���ʽ��������
					tempstr = "+" + split_2[i];
				} else if (split_2.length == 1) { // ����һ���򲻴��Ӻ�
					tempstr = split_2[i];
				}
				// System.err.println("tempstr: " + tempstr);
				break;
			}
		}

		if (isok) {
			result_b = right.replace(tempstr, "");
			// System.err.println("result_a: " + result_a);
			// System.err.println("result_b: " + result_b);
			String temp = "";
			if (result_a.length() > 1 && result_b.contains("+")) { // ���������ʽ
				temp = left + "=(" + result_a + ")*" + "(" + result_b + ")";
			} else if (result_a.length() > 1) {
				temp = left + "=(" + result_a + ")*" + result_b;
			} else if (result_b.contains("+")) {
				temp = left + "=" + result_a + "*" + "(" + result_b + ")";
			} else {
				temp = left + "=" + result_a + "*" + result_b;
			}

			if (left.equals("S")) {
				finalResult = temp;
				// System.err.println(finalResult);
			} else {
				temp = this.distribute(temp); // ��ʽ�ֽ�
				this.result.add(temp);
			}
			// System.out.println(temp);
		} else {
			if (left.equals("S")) {
				finalResult = str;
			} else {
				this.result.add(str);
			}
		}

	}

	public void replaces() {
		for (int i = 0; i < result.size(); i++) { // ���ɴ����Ĳ���ʽ���д���
			String result_i = result.get(i);
			String left = this.getLeft(result_i);
			String right = this.getRight(result_i);
			for (int j = 0; j < result.size(); j++) {
				if (!result.get(j).equals(result_i)
						&& result.get(j).contains(left)) { // �����������ʽ�����˴���ʽ�����
					// System.out.println("left:" + left + "  right:" + right);
					if (right.contains("+")) {
						result.set(j,
								result.get(j).replace(left, "(" + right + ")")); // �滻
					} else {
						result.set(j, result.get(j).replace(left, right)); // �滻
					}
					// System.out.println("�滻��" + result.get(j));
				}
			}
		}
	}

	public String analyze(int num) {
		String result = "";
		for (int i = 0; i < num; i++) {
			this.analyzeOne(input.get(i));
		}

		this.replaces(); // �滻���ֲ���ʽ����Ĳ���ʽ

		for (int i = 0; i < this.result.size(); i++) { // �滻S����ʽ
			String left = this.getLeft(this.result.get(i));
			String right = this.getRight(this.result.get(i));
			// System.out.println("left:" + left + "  right:" + right);
			if (this.finalResult.contains(left)) { // �����������ʽ�����˴���ʽ�����
				if (right.contains("+")) {
					this.finalResult = this.finalResult.replace(left, "("
							+ right + ")"); // �滻
				} else {
					this.finalResult = this.finalResult.replace(left, right); // �滻
				}
				// System.out.println("�滻��finalResult��" + this.finalResult);
			}
		}

		this.finalResult = this.distribute(this.finalResult);
		// System.out.println("�ֽ��finalResult��" + this.finalResult);
		this.analyzeOne(this.finalResult);
		// System.out.println("���finalResult��" + this.finalResult);

		return result;
	}

	/**
	 * ��ȡ������
	 * 
	 * @param args
	 */
	public String getResult() {
		return this.finalResult;
	}

	public static void main(String args[]) {
		GramToExpr gte = new GramToExpr();
		// gte.input.add("S=aS+aB");
		// gte.input.add("B=bC");
		// gte.input.add("C=aC+a");
		gte.input.add("S=cC+a");
		gte.input.add("A=cA+aB");
		gte.input.add("B=aB+c");
		gte.input.add("C=aS+aA+bB+cC+a");
		// gte.addInput("S=aS+bB");
		// gte.addInput("B=aC+bB");
		// gte.addInput("C=cC+b");
		gte.analyze(gte.getInputSize());
		System.out.println("Result:" + gte.getResult());

	}

}
