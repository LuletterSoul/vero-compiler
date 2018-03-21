package com.vero.compiler.scan.core;


public class GrammarConverter {

	private String input_str = ""; // ���������ַ���

	public GrammarConverter() {

	}

	public GrammarConverter(String str) {
		this.input_str = str;
	}

	/**
	 * �ж�ת������
	 * 
	 * @return 1 S-a 2 A-a 3 S-aA��S-Aa 4A-Ba��B-aA
	 */

	public int check(String left, String right) {
		int result = 0;
		boolean isLower = true;

		for (int i = 0; i < right.length(); i++) { // ������ʽ�ұ��Ƿ��з��ս��
			if (Character.isUpperCase(right.charAt(i))) {
				isLower = false;
			}
		}

		if (isLower) { // �ұ߶��Ƿ��ս��
			if (left.equals("S")) {
				result = 1;
			} else {
				result = 2;
			}
		} else {
			if (left.equals("S")) {
				result = 3;
			} else {
				result = 4;
			}
		}

		return result;
	}

	/**
	 * ���������ķ�ת������ �������ʽ�����á�-������������ S-Aa��Լ�� SΪ��ʼ����
	 * 
	 * @param input
	 *            �����ַ��� aim 1 תΪ������ 2 תΪ������ check���� 1 S-a 2 A-a 3 S-aA��S-Aa
	 *            4A-Ba��B-aA
	 * @return
	 */
	public String convert(String input, int aim) {
		String result = "";
		String left = "";
		String right = "";
		int length = -1;

		if (input.contains("-")) {
			String[] split = input.split("-");
			left = split[0];
			right = split[1];
			length = right.length();
		} else {
			return "";
		}

		if (aim == 1) { // ������ת��Ϊ�������ķ�
			switch (this.check(left, right)) {
			case 1:
				result = left + "-" + right;
				break;
			case 2:
				result = "S-" + left + right;
				break;
			case 3:
				result = right.charAt(length - 1) + "-"
						+ right.substring(0, length - 1);
				break;
			case 4:
				result = right.charAt(length - 1) + "-" + left
						+ right.substring(0, length - 1);
				break;
			}
		} else if (aim == 2) { // ������תΪ������
			switch (this.check(left, right)) {
			case 1:
				result = left + "-" + right;
				break;
			case 2:
				result = "S-" + right + left;
				break;
			case 3:
				result = right.charAt(0) + "-" + right.substring(1, length);
				break;
			case 4:
				result = right.charAt(0) + "-" + right.substring(1, length)
						+ left;
				break;
			}
		}

		return result;
	}
	
	public static void main (String args[]){
		GrammarConverter  gc= new GrammarConverter();
		System.out.println(gc.convert("A-a", 1));
	}
}
