package com.personal.java_code_check;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class AppStartCheckPublicJavaTestClassesTest {

	@Test
	void testMain() throws Exception {

		final String folderPathString;
		final int input = Integer.parseInt("11");
		if (input == 1) {
			folderPathString = Paths.get("").toAbsolutePath()
					.getParent().getParent().getParent().getParent().toString();

		} else if (input == 11) {
			folderPathString = "C:\\IVI\\Vitesco\\Main";

		} else {
			throw new RuntimeException();
		}

		AppStartCheckPublicJavaTestClasses.main(folderPathString);
	}
}
