package skunk.slack.crawler.data.core.hibernate;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class SnakeCaseNamingStrategy implements PhysicalNamingStrategy {
	@Override
	public Identifier toPhysicalCatalogName(Identifier arg0, JdbcEnvironment arg1) {
		return convert(arg0, arg1);
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier arg0, JdbcEnvironment arg1) {
		return convert(arg0, arg1);
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier arg0, JdbcEnvironment arg1) {
		return convert(arg0, arg1);
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier arg0, JdbcEnvironment arg1) {
		return convert(arg0, arg1);
	}

	@Override
	public Identifier toPhysicalTableName(Identifier arg0, JdbcEnvironment arg1) {
		return convert(arg0, arg1);
	}

	private Identifier convert(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
		if (Objects.isNull(identifier)) {
			return null;
		}
		return jdbcEnvironment.getIdentifierHelper()
				.toIdentifier(toSnakeCase(identifier.getText()), identifier.isQuoted());
	}
	
	private String toSnakeCase(String camelCase) {
		Iterable<String> splittedText = Splitter.on(Pattern.compile("((?<=[^A-Z0-9])(?=[A-Z0-9])|(?=[A-Z][^A-Z]))"))
				.omitEmptyStrings().splitToList(camelCase)
				.stream().map(str -> str.toLowerCase())
				.collect(Collectors.toList());
		return Joiner.on("_").skipNulls().join(splittedText);
	}
}
