# Project Valhalla

Example code for the HappyCoders.eu article on Project Valhalla and value objects in Java 28 – JEP 401 (Value Objects) and JEP 539 (Strict Field Initialization in the JVM). The article will appear in German and English on [happycoders.eu](https://www.happycoders.eu/).

## Requirements

Value objects are a preview feature of Java 28. You need an early-access build of JDK 28 ([jdk.java.net/28](https://jdk.java.net/28/)) and must enable preview features. All outputs in the article and in `results/` come from build 28-ea+23; layouts and diagnostic options may change until the release.

## Programs

The programs are compact source files in the unnamed package, one directory per topic. Value classes that are not records live in their own files: a class declared in a compact source file is an inner class of the implicitly declared class, and an inner value class gets a hidden `this$0` field that makes it larger and harder to flatten. Records are implicitly static and are not affected.

Run a program from its directory with the source launcher, which also compiles the other files it needs from the same directory:

```
cd src/main/java/declaring
java --enable-preview MoneyDemo.java
```

| Directory | Programs |
|---|---|
| `motivation` | `IdentitySurprises` (`==` on `Integer` and `LocalDate`, run with and without preview), `WrapperConstructor` |
| `jdk` | `HasIdentity` – `Objects.hasIdentity()` on JDK classes |
| `declaring` | `MoneyDemo` with the value class `Money` |
| `equality` | `PersonComparison`, `FractionDemo`, `NaNValues`, `PixelDemo`, `DeepComparison` – where `==` and `equals()` differ |
| `restrictions` | `IdentityOperations`, `WeakRefWarning`, `CloneValue` – operations that need identity |
| `inheritance` | `Numbers`, `ShapesDemo` – abstract value classes |
| `construction` | `UserNameDemo`, `RecordThisLeak` – the constructor rules |
| `serialization` | `broken/SerializeValues` and `proxy/SerializeMoney` – serialization with `writeReplace()`/`readResolve()` |
| `reflection` | `ReflectValue` – deep reflection on identity and value classes |
| `layout` | `PrintLayouts` – loads the classes whose field and array layouts the article shows |
| `scalarization` | `Scalarization` – allocated bytes per call for value and identity records |
| `header` | `MarkWord` – reads the mark word of a few objects with `sun.misc.Unsafe` |
| `footprint` | `DateArrayFootprint`, `PointArrayFootprint` – heap class histograms |

`src/compile-errors` holds the examples that the compiler rejects, one directory per topic.

## Layouts

`PrintLayouts` prints the field layouts of all loaded classes with diagnostic VM options:

```
cd src/main/java/layout
java --enable-preview -Xshare:off -XX:+UnlockDiagnosticVMOptions \
     -XX:+PrintFieldLayout -XX:+PrintFlatArrayLayout PrintLayouts.java
```

`-Xshare:off` makes the JDK classes such as `LocalDate` appear in the output; otherwise the JVM loads them from the CDS archive without printing their layout.

## Reproducing all outputs

`scripts/run-examples.sh <path-to-jdk-28>` runs every program with the options used in the article, compiles the compile-error examples, and writes all outputs to `results/<build>/`. It takes about half a minute.
