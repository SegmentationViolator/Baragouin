# Baragouin
A text generator that can generate Markov chains from text and generate text from the generated Markov chains

## Usage

### Prerequisites
- Java Runtime Environment 24 or later
- JAR package from [releases](https://github.com/SegmentationViolator/Baragouin/releases/latest/)

### Generate a Markov chain

```
java -jar baragouin-0.1.0.jar generate-chain -t sample.txt -o chain.json
```

### Generate text using previously generated Markov chain

```
java -jar baragouin-0.1.0.jar generate-chain chain.json
```

use `java -jar baragouin-0.1.0.jar help` for more details

## Dependencies

- [FasterXML/jackson-jr](https://github.com/FasterXML/jackson-jr)
- [remkop/picocli](https://github.com/remkop/picocli)
