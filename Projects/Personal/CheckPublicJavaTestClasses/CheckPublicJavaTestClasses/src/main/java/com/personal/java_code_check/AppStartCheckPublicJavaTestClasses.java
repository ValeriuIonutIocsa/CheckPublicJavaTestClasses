package com.personal.java_code_check;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class AppStartCheckPublicJavaTestClasses {

	private AppStartCheckPublicJavaTestClasses() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		if (args.length == 0) {

			final String helpMessage = createHelpMessage();
			System.err.println("insufficient arguments" + System.lineSeparator() + helpMessage);
			System.exit(-1);
		}

		if ("-help".equals(args[0])) {

			final String helpMessageString = createHelpMessage();
			System.out.println(helpMessageString);
			System.exit(0);
		}

		final String folderPathString = args[0];

		boolean success = false;
		try {
			success = main(folderPathString);

		} catch (final Exception exc) {
			exc.printStackTrace();
		}

		if (!success) {
			System.exit(-2);
		}

		final Duration executionTime = Duration.between(start, Instant.now());
		System.out.println("done; execution time: " + durationToString(executionTime));
	}

	private static String createHelpMessage() {
		return "usage: check_public_java_test_classes DIR_PATH";
	}

	static boolean main(
			final String folderPathString) throws Exception {

		boolean success = false;

		System.out.println();
		System.out.println("--> starting check public java test classes");

		final Path folderPath = Paths.get(folderPathString).toAbsolutePath().normalize();
		if (!Files.isDirectory(folderPath)) {
			System.err.println("ERROR - folder does not exist");

		} else {
			System.out.println("folder path:");
			System.out.println(folderPathString);

			final List<Path> javaFilePathList;
			try (Stream<Path> filePathStream = Files.walk(folderPath)) {

				javaFilePathList = filePathStream
						.filter(filePath -> filePath.toString().endsWith(".java"))
						.collect(Collectors.toList());
			}
			final List<Path> publicTestClassPathList = new ArrayList<>();
			for (final Path javaFilePath : javaFilePathList) {

				final boolean publicTestClass = checkPublicTestClass(javaFilePath);
				if (publicTestClass) {
					publicTestClassPathList.add(javaFilePath);
				}
			}
			if (publicTestClassPathList.isEmpty()) {
				System.out.println("found no public java test classes");

			} else {
				System.out.println("found " + publicTestClassPathList.size() + " public java test classes:");
				for (final Path publicTestClassPath : publicTestClassPathList) {
					System.out.println(publicTestClassPath);
				}
			}
			success = true;
		}
		return success;
	}

	private static boolean checkPublicTestClass(
			final Path javaFilePath) throws Exception {

		final List<String> trimmedLineList = Files.readAllLines(javaFilePath).stream()
				.map(String::trim).collect(Collectors.toList());

		boolean testClass = false;
		for (final String trimmedLine : trimmedLineList) {

			if (trimmedLine.startsWith("@Test")) {

				testClass = true;
				break;
			}
		}

		boolean publicClass = false;
		for (final String trimmedLine : trimmedLineList) {

			if (trimmedLine.startsWith("public class")) {

				publicClass = true;
				break;
			}
		}

		return testClass && publicClass;
	}

	private static String durationToString(
			final Duration duration) {

		final StringBuilder stringBuilder = new StringBuilder();
		final long allSeconds = duration.get(ChronoUnit.SECONDS);
		final long hours = allSeconds / 3600;
		if (hours > 0) {
			stringBuilder.append(hours).append("h ");
		}

		final long minutes = (allSeconds - hours * 3600) / 60;
		if (minutes > 0) {
			stringBuilder.append(minutes).append("m ");
		}

		final long nanoseconds = duration.get(ChronoUnit.NANOS);
		final double seconds = allSeconds - hours * 3600 - minutes * 60 +
				nanoseconds / 1_000_000_000.0;
		stringBuilder.append(doubleToString(seconds)).append('s');

		return stringBuilder.toString();
	}

	private static String doubleToString(
			final double d) {

		final String str;
		if (Double.isNaN(d)) {
			str = "";

		} else {
			final String format;
			format = "0.000";
			final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
			str = decimalFormat.format(d);
		}
		return str;
	}
}
