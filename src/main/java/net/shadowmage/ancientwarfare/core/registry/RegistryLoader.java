package net.shadowmage.ancientwarfare.core.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.IModInfo;

import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class RegistryLoader {
	private RegistryLoader() {}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Map<String, IRegistryDataParser> parsers = new HashMap<>();

	public static void registerParser(IRegistryDataParser parser) {
		parsers.put(parser.getName(), parser);
	}

	private static final Map<ResourceLocation, String> loadedRegistries = new HashMap<>();

	private static final List<DependentFile> loadLater = new ArrayList<>();

	public static void load() {
		load(p -> true);
	}

	public static void reload(String type) {
		loadedRegistries.entrySet().removeIf(entry -> entry.getValue().equals(type));
		load(p -> p.getName().equals(type));
	}

	public static void load(Predicate<IRegistryDataParser> include) {
		Optional<? extends ModContainer> awModContainerOpt = ModList.get().getModContainerById("ancientwarfare" /* TODO Phase 9: AncientWarfareCore.MOD_ID */);
		if (!awModContainerOpt.isPresent()) return;
		ModContainer awModContainer = awModContainerOpt.get();

		Path registryOverridesFolder = new File(AWCoreStatics.configPathForFiles + "registry").toPath();
		if (registryOverridesFolder.toFile().exists()) {
			//noinspection ConstantConditions
			loadRegistries(awModContainer, registryOverridesFolder, include);
		}
		//noinspection ConstantConditions
		loadRegistries(awModContainer, awModContainer.getModInfo().getOwningFile().getFile().getFilePath().toFile(), "assets/" + awModContainer.getModId() + "/registry", include);
	}

	private static void loadRegistries(ModContainer mod, File source, String base, Predicate<IRegistryDataParser> include) {
		if (!(source.isDirectory() || source.isFile())) {
			return;
		}

		FileSystem fs = null;
		try {
			Path root;
			if (source.isFile()) {
				fs = FileSystems.newFileSystem(source.toPath(), (ClassLoader) null);
				root = fs.getPath("/" + base);
			} else {
				root = source.toPath().resolve(base);
			}

			loadRegistries(mod, root, include);
		}
		catch (IOException e) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("Error loading FileSystem from jar: ", e).replace("{}", "%s"));
		}
		finally {
			IOUtils.closeQuietly(fs);
		}
	}

	@SuppressWarnings("squid:S3725") //ZipPath doesn't have toFile support
	private static void loadRegistries(ModContainer mod, Path root, Predicate<IRegistryDataParser> include) {
		if (!Files.exists(root)) {
			return;
		}

		Iterator<Path> itr;
		try {
			itr = Files.walk(root).iterator();
		}
		catch (IOException e) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("Error iterating filesystem for: {}", root, e).replace("{}", "%s"));
			return;
		}
		while (itr.hasNext()) {
			loadFile(mod, root, itr.next(), include);
		}

		loadDependents(mod, include);
	}

	private static void loadDependents(ModContainer mod, Predicate<IRegistryDataParser> include) {
		int lastCountLoadLater = loadLater.size();
		while (!loadLater.isEmpty()) {
			Iterator<DependentFile> iterator = loadLater.iterator();
			while (iterator.hasNext()) {
				DependentFile dependentFile = iterator.next();
				if (areDependenciesLoaded(dependentFile.getDependencies())) {
					loadFile(mod, dependentFile.getPath(), include, false, dependentFile.getName());
					iterator.remove();
				}
			}
			if (lastCountLoadLater <= loadLater.size()) {
				logIncorrectDependencies();
				break;
			}
			lastCountLoadLater = loadLater.size();
		}
	}

	private static void logIncorrectDependencies() {
		for (DependentFile dependentFile : loadLater) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("Non existent or circular load after dependencies in {} - {}", dependentFile.getPath().toString(), String.join(",", dependentFile.getDependencies())).replace("{}", "%s"));
		}
	}

	private static void loadFile(ModContainer mod, Path root, Path file, Predicate<IRegistryDataParser> include) {


		if (!"json".equals(FilenameUtils.getExtension(file.toString()))) {
			return;
		}

		String relative = root.relativize(file).toString();
		String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");

		loadFile(mod, file, include, true, name);
	}

	private static void loadFile(ModContainer mod, Path file, Predicate<IRegistryDataParser> include, boolean checkDependencies, String name) {
		String shortName = name.substring(name.lastIndexOf('/') + 1);

		ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath(mod.getModId(), name);

		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(file);
			JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);

			if (json == null) {
				return;
			}


			Optional<IRegistryDataParser> parser = getParser(shortName, json);

			if (!parser.isPresent()) {
				System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("No parser defined for file name {}", shortName).replace("{}", "%s"));
				return;
			}

			if (checkDependencies && json.has("load_after")) {
				Set<String> dependencies = new java.util.HashSet<>() /* TODO Phase 2/4: JsonHelper.setFromJson */;
				if (!areDependenciesLoaded(dependencies)) {
					loadLater.add(new DependentFile(name, file, dependencies));
					return;
				}
			}

			if (loadedRegistries.containsKey(registryName)) {
				System.out.println /* TODO Phase 9: AncientWarfareCore.LOG.info */ (String.format("Registry {} has already been loaded in overrides, skipping...", registryName.toString()).replace("{}", "%s"));
				return;
			}
			loadedRegistries.put(registryName, parser.get().getName());

			if (!include.test(parser.get()) || isDisabled(json) || !isModLoaded(json)) {
				return;
			}

			parser.get().parse(json);
		}
		catch (JsonParseException e) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("Parsing error loading registry {}", registryName, e).replace("{}", "%s"));
		}
		catch (MissingResourceException e) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format(e.getMessage()).replace("{}", "%s"));
		}
		catch (IOException e) {
			System.err.println /* TODO Phase 9: AncientWarfareCore.LOG.error */ (String.format("Couldn't read registry {} from {}", registryName, file, e).replace("{}", "%s"));
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}

	private static boolean areDependenciesLoaded(Set<String> dependencies) {
		for (String dependency : dependencies) {
			if (!loadedRegistries.containsValue(dependency)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isModLoaded(JsonObject json) {
		return !json.has("mod") || ModList.get().isLoaded(GsonHelper.getAsString(json, "mod"));
	}

	private static boolean isDisabled(JsonObject json) {
		return json.has("disabled") && GsonHelper.getAsBoolean(json, "disabled");
	}

	private static Optional<IRegistryDataParser> getParser(String fileName, JsonObject json) {
		String parserName = fileName;
		if (json.has("type")) {
			parserName = GsonHelper.getAsString(json, "type");
		}
		return parsers.containsKey(parserName) ? Optional.of(parsers.get(parserName)) : Optional.empty();
	}

	private static class DependentFile {
		private final String name;
		private final Path path;
		private final Set<String> dependencies;

		private DependentFile(String name, Path path, Set<String> dependencies) {
			this.name = name;
			this.path = path;
			this.dependencies = dependencies;
		}

		public Set<String> getDependencies() {
			return dependencies;
		}

		public Path getPath() {
			return path;
		}

		public String getName() {
			return name;
		}
	}
}
