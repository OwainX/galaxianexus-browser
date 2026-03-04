# Contributing to GalaxiaNexus Browser

Thank you for your interest in contributing to GalaxiaNexus Browser! We welcome contributions from the community.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/galaxianexus-browser.git
   cd galaxianexus-browser
   ```
3. **Set up the development environment**:
   ```bash
   ./gradlew genSources
   ```

## Development Workflow

### Running the Mod in Development

```bash
./gradlew runClient
```

This will launch Minecraft with your mod loaded in a development environment.

### Building the Mod

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

### Code Style

- Use 4 spaces for indentation (no tabs)
- Follow standard Java naming conventions
- Add JavaDoc comments for public methods and classes
- Keep methods focused and concise
- Write descriptive variable names

### Project Structure

```
src/main/java/com/galaxianexus/browser/
├── GalaxiaNexusBrowserMod.java   # Main mod entry point
├── BrowserManager.java            # Core browser logic
├── BrowserScreen.java             # UI rendering
└── WebContentRenderer.java        # Web content handling
```

## Making Changes

1. **Create a new branch** for your feature or fix:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the code style guidelines

3. **Test your changes** by running the mod:
   ```bash
   ./gradlew runClient
   ```

4. **Commit your changes** with clear, descriptive messages:
   ```bash
   git commit -m "Add feature: description of what you added"
   ```

5. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Open a Pull Request** on GitHub

## Pull Request Guidelines

- **Title**: Use a clear, descriptive title
- **Description**: Explain what changes you made and why
- **Testing**: Describe how you tested your changes
- **Screenshots**: If UI changes, include screenshots
- **Link Issues**: Reference any related issues

## Areas for Contribution

### Beginner-Friendly

- Documentation improvements
- Bug fixes
- UI enhancements
- Icon and asset creation

### Intermediate

- New browser features (bookmarks, history, etc.)
- Performance optimizations
- Better HTML parsing
- Cookie management

### Advanced

- JavaScript execution support
- Server-side packet integration
- Multi-tab support
- Advanced rendering engine integration

## Code Review Process

1. A maintainer will review your PR
2. You may be asked to make changes
3. Once approved, your PR will be merged
4. Your contribution will be credited in the release notes

## Reporting Bugs

Use the [GitHub Issues](https://github.com/yourusername/galaxianexus-browser/issues) page to report bugs.

Include:
- Minecraft version
- Fabric Loader version
- Mod version
- Steps to reproduce
- Expected behavior
- Actual behavior
- Logs (if applicable)

## Feature Requests

We welcome feature requests! Open an issue with:
- Clear description of the feature
- Use case / why it's needed
- Potential implementation ideas (optional)

## Questions?

Feel free to open an issue for questions or join our community discussions.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing! 🚀
