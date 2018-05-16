> git地址：https://github.com/chrisdchristo/capsule-maven-plugin

> demo git 地址： https://github.com/chrisdchristo/capsule-maven-plugin-demo
#### Capsule Maven Plugin

===翻译自：https://github.com/chrisdchristo/capsule-maven-plugin#maven-exec-plugin-integration===

> 一个Maven插件，用于从您的应用程序中构建一个capsule
- Capsule | simple java deployment
- Capsule & AWS | Java on the cloud

> 条件： java version 1.7+ ， maven 3.1.x+

### Building From source
> clone the project and run a maven install

```
git clone https://github.com/chrisdchristo/capsule-maven-plugin.git
cd capsule-maven-plugin
mvn install

```

### Quick Start

> 用最简单的形式，你可以在你的pom.xml中添加下面的代码片段：

```
 <properties>
    <capsule.maven.plugin.version>1.5.1</capsule.maven.plugin.version>
  </properties>
  
 <build>
    <plugins>
      <plugin>
        <groupId>com.github.chrisdchristo</groupId>
        <artifactId>capsule-maven-plugin</artifactId>
        <version>${capsule.maven.plugin.version}</version>
        <configuration>
          <appClass>com.evelyn.mavenplugin.study.launch.SimpleFormApplication</appClass>
          <type>fat</type>
        </configuration>
      </plugin>
    </plugins>
  </build>
```
> and then run :

```
mvn package capsule:build
```
> 请注意：package命令必须在capsule：build之前运行。

> 唯一要求是配置中具有<appClass>属性，这是程序启动类，其中包含将在启动时触发的main方法。必须是全路径名。

> 建议在pom中指定capsule版本属性，以确定capsule使用版本。如果没有指定，则默认版本的capsule将按照自述文件顶部的指定使用（可能不是最新版本）

> 生成jar： 服务名-版本号-capsule.jar

> 启动jar：java -jar 服务名-版本号-capsule.jar


### 自动构建

> 建议有一个执行安装程序来构建capsule，从而不需要运行其他maven命令来构建它们。

```
<build>
    <plugins>
      <plugin>
        <groupId>com.github.chrisdchristo</groupId>
        <artifactId>capsule-maven-plugin</artifactId>
        <version>${capsule.maven.plugin.version}</version>
        <executions>
          <execution>
            //目标
            <goals>
              <goal>build</goal> //构建目标
            </goals>
            <configuration>
              <appClass>com.evelyn.mavenplugin.study.launch.BuildingAutoApplication</appClass>
              <type>fat</type>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
> 默认情况下，build在package阶段运行

> so now run:

```
mvn package
```
> 那么构建目标将会执行，它会把capsule构建到target目录中

> 或者也可使用maven-exec-plugin运行，然后只在想要部署到服务器时构建capsule。see [here](https://github.com/chrisdchristo/capsule-maven-plugin#maven-exec-plugin-integration)

### Capsule type
> 本质上capsule可以随意或尽可能小地package

> 你需要考虑两件事来组成一个capsule，应用程序jar和依赖kar。正如你将会看到的，这些可以选择性的包括在内。

> 依赖关系的来源来自两个地方。
- 首先，从根```<project>```标记下定义的```<dependencies>```标记，即应用程序依赖项。
- 其次，在这个插件的```<plugin>```标签内的```<dependencies>```标签下定义的依赖关系，也被称为插件依赖关系。

> 可以选择包含all，none或者some of the dependencies.

> 这可以通过各种标志来完成，根据它们的来源、作用域，可选标志以及它们是直接（根）还是间接（传递）依赖关系

#### The Simple Types
> 一般来说，最常见的capsule类型有一下三种：
- ```fat```：这个capsule jar将会包含应用程序的jar以及它的所有依赖项。当运行fat-jar时，capsule将简单的设置应用程序并运行它。
- ```thin```：这个capsule jar将会包含应用程序的类，但没有依赖关系。capsule将在运行时解决这些依赖关系（在缓存中）。
- ```empty```：这个capsule jar甚至不包括应用程序或其任何依赖项。它只包含在jar的清单中声明的应用程序的名称，以及capsule的类。Capsule将读取清单条目应用程序并解析capsule自己的缓存中的应用程序及其依赖关系（默认为~/.capsule）。

> 插件提供了一个简单的标志来构建这些类型，即可以将<type>字段设置为fat,thin or empty.

```
 <build>
    <plugins>
      <plugin>
        <groupId>com.github.chrisdchristo</groupId>
        <artifactId>capsule-maven-plugin</artifactId>
        <version>${capsule.maven.plugin.version}</version>
        <configuration>
          <appClass>com.evelyn.mavenplugin.study.launch.SimpleFormApplication</appClass>
          <type>fat</type> // capsule 类型
        </configuration>
      </plugin>
    </plugins>
  </build>
```
> 注意：这三种简单类型仅适用于编译和运行时范围的依赖关系（但涵盖了传递依赖关系）。稍后再说。

> 如果这些都不适合，那么该插件可以适应各种不同的设置，鼓励根据自己的具体要求构建capsule，而不会陷入上面列出的三种特定类型

### 自定义构建（Custom Builds）
> ==注意：要进行自定义构建，不得设置<type>标记！==

> 如果<type>中定义的类型不太适合您需求，并且您需要一些不同的东西，那么您可以轻松地将jar定制为大量的选项。

> 基本上它归结为一些情况：是否包含应用程序或在运行时解决它；根据它们的来源，作用域等来包含哪些依赖关系，以及在运行时解决哪些依赖关系；以及传递性依赖的相同问题。

> 为了涵盖所有这些想法，有以下标志：

```
<includeApp>false</includeApp> // 包含应用程序启动app

<includeAppDep>false</includeAppDep>    // 包含基于源的依赖关系：应用程序依赖项

<includePluginDep>false</includePluginDep> // 包含基于源的依赖关系：插件依赖项

<includeTransitiveDep>false</includeTransitiveDep>
<includeCompileDep>false</includeCompileDep>

<includeRuntimeDep>false</includeRuntimeDep> // 包含基于scope的依赖关系：scope为runtime

<includeProvidedDep>false</includeProvidedDep>
<includeSystemDep>false</includeSystemDep>
<includeTestDep>false</includeTestDep>

<includeOptionalDep>false</includeOptionalDep> // 包含

<resolveApp>false</resolveApp>
<resolveAppDep>false</resolveAppDep>
<resolvePluginDep>false</resolvePluginDep>
<resolveTransitiveDep>false</resolveTransitiveDep>
<resolveCompileDep>false</resolveCompileDep>
<resolveRuntimeDep>false</resolveRuntimeDep>
<resolveProvidedDep>false</resolveProvidedDep>
<resolveSystemDep>false</resolveSystemDep>
<resolveTestDep>false</resolveTestDep>
<resolveOptionalDep>false</resolveOptionalDep>
```
> 以上所有设置默认为 ```false```

> 这些 ```includeXYZ```标志基本上告诉插件在jar包中包含/嵌入什么，当然，如果有任何这些从capsule jar中排除，并且反过来需要它们进行启动，那么通过将某些```resolveXYZ```标记为```true```，那么将会在运行时解析。

> ```fat```  capsule type 基本相当于只有下列设置为真：

```
<includeApp>true</includeApp>
<includeAppDep>true</includeAppDep>
<includePluginDep>true</includePluginDep>
<includeCompileDep>true</includeCompileDep>
<includeRuntimeDep>true</includeRuntimeDep>
<includeTransitiveDep>true</includeTransitiveDep>
```
> so as you can see,```fat```包含了应用程序，以及它的所有编译和运行时依赖项（包括传递依赖项）

> so ```thin``` capsule type , 配置如下：

```
<includeApp>true</includeApp>

<resolveAppDep>true</resolveAppDep>
<resolvePluginDep>true</resolvePluginDep>
<resolveCompileDep>true</resolveCompileDep>
<resolveRuntimeDep>true</resolveRuntimeDep>
<resolveTransitiveDep>true</resolveTransitiveDep>
```
> 同样，```empty``` capsule type 如下配置：

```
<resolveApp>true</resolveApp>
<resolveAppDep>true</resolveAppDep>
<resolvePluginDep>true</resolvePluginDep>
<resolveCompileDep>true</resolveCompileDep>
<resolveRuntimeDep>true</resolveRuntimeDep>
<resolveTransitiveDep>true</resolveTransitiveDep>
```

### 包含基于源的依赖关系（based on source）
> 依赖关系的来源来自两个地方。
- 首先，从根```<project>```标记下定义的```<dependencies>```标记，即应用程序依赖项。
- 其次，在这个插件的```<plugin>```标签内的```<dependencies>```标签下定义的依赖关系，也被称为插件依赖关系。

> 可以使用```<includeAppDep>true</includeAppDep>``` 或 
```<includePluginDep>true</includePluginDep>```来选择包含源。

### 包含基于范围的依赖关系 （based on scope）
> 您可以通过将依赖项的scope设置为您将包含在built capsule中的某些内容来包含某些依赖项

> scope : ```compile```,```runtime```,```provided```,```system```,```test```

> plugin dependencies scope only have:```compile```,```runtime```,```system```

> 所以你可以设置你的依赖范围运行（scope：```runtime```）时像这样：

```
<dependency>
	<groupId>com.google.guava</groupId>
	<artifactId>guava</artifactId>
	<version>17.0</version>
	<scope>runtime</scope>
</dependency>
```
> 然后在pom中标记必要的配置：

```
<configuration>
    <includeRuntimeDep>true</includeRuntimeDep>
</configuration>

```
> 或者使用 ```resolve``` 代替：

```
<configuration>
    <resolveRuntimeDep>true</resolveRuntimeDep>
</configuration>
```
> 这个不会包含用运行时范围标记的依赖关系，但会在启动时解决他们。

> 确保您的source也设置为```true``` ，使用```<includeRuntimeDep>true</includeRuntimeDep>``` 或者 ```<resolveRuntimeDep>true</resolveRuntimeDep>```

### 包含可选的依赖项（optional dependencies）
> 依赖关系可以用```<optional>```标记来标记，例如：

```
<dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>17.0</version>
      <optional>true</optional>
</dependency>
```
> 为了在capsule中包含```<optional>```依赖项，只需要设置如下：

```
<configuration>
	<includeOptionalDep>true</includeOptionalDep>
</configuration>
```
> or

```
<configuration>
	<resolveOptionalDep>true</resolveOptionalDep>
</configuration>
```
> 确保你的source给这个也设置了true： ```<includeAppDep>true</includeAppDep>``` or ```<resolveAppDep>true</resolveAppDep>```

### 包含传递相关性（transitive dependencies）
> 依赖传递本质上是深层依赖或其他依赖关系的依赖关系

> 可以通过配置属性```includeTransitiveDep``` 设置为```true```来包含传递依赖关系：

```
<configuration>
	<includeTransitiveDep>true</includeTransitiveDep>
</configuration>
```
> or

```
<configuration>
	<resolveTransitiveDep>true</resolveTransitiveDep>
</configuration>
```
> Just make sure you have a source also set to true for example, ```<includeAppDep>true</includeAppDep>``` or ```<resolveAppDep>true</resolveAppDep>```.

### 理解依赖范围：dependency scope
> maven的依赖定义scope：```compile```,```runtime```,```provided```,```system```,```test```.

> 你可以在每个project的直接依赖关系上设置范围。虽然传递依赖关系也会定义范围，但这仅适用于他们自己的project。

> 与主project相关的传递依赖的范围将直接受其父项依赖的范围的影响。我们称之为‘直接范围’。

> 还要注意：带有除编译或运行时之外的范围的依赖传递不适用于主project，因此总是被排除的。

> so，对于每个与范围直接相关的内容：
* ```compile```
    - ```compile``` transitive dependencies have ```compile``` direct-scope.
    - ```runtime``` transitive dependencies have ```runtime``` direct-scope.
* ```runtime```
	- ```compile``` transitive dependencies have ```compile``` direct-scope.
	- ```runtime``` transitive dependencies have ```runtime``` direct-scope.
* ```provided```
	- ```compile``` & ```runtime``` transitive dependencies have ```provided``` direct-scope.
* ```system```
    - ```compile``` & ```runtime``` transitive dependencies have ```system``` direct-scope.
* ```test```
	- ```compile``` & ```runtime``` transitive dependencies have ```test``` direct-scope.

So, all the ```includeXYZ``` and ```resolveXYZ``` follow the above rules.


### 运行时解决方案：Runtime Resolution
> 要在运行时执行解析（例如```thin```和```empty```类型）,capsule将包含执行此操作所需的代码（即MavenCaplet）。这就稍微增加了生成的capsule的整个文件大小。如果需要在运行时解析任何依赖关系（或应用程序本身），则此附加代码显然是强制的。

> 要在没有此附加代码的情况下build capsule，请确保没有任何 ```resolveXYZ```标志设置为```true```（默认情况下全部设置为```false```或者```<type>```设置```fat```）。

> 如果在运行时需要自定义生成和解析，那么在```<configuration>```标记中添加所需的```resolveXYZ```标记，如下：

```
<configuration>
	<appClass>com.evelyn.mavenplugin.study.launch.SimpleFormApplication</appClass>
	
	<resolveApp>true<resolveApp>
	<resolveCompileDep>true<resolveCompileDep>
</configuration>
```

### Really Executable Capsules (Mac/Linux only)
> 有可能 ```chomd+x```是一个jar文件，因此可以在不需要用```java -jar```命令前缀的情况下运行它。

> 该插件可以为你自动构建真正可执行的jar包

> 将```<chmod>true</chmod>```添加到您的配置中（默认为false）

```
<configuration>
	<appClass>com.evelyn.mavenplugin.study.launch.SimpleFormApplication</appClass>
	<chmod>true</chmod>
</configuration>
```
> 该插件将输出具有扩展名```.x```的真正可执行文件。

```
target/my-app-1.0-cap.jar
target/my-app-1.0-cap.x
```

> 所以通常你会像这样运行capsule：

```
java -jar target/my-app-1.0-cap.jar
```
> 然而，使用really executable（真正可执行版本）构建的话，你可以选择性的运行capsule：

```
./target/my-app-1.0-cap.x
```
> or

```
sh target/my-app-1.0-cap.x
```

##### Trampoline
> 启动capsule时，涉及两个过程：首先，JVM进程运行capsule启动程序，然后启动第二个运行实际应用程序的子进程。这两个过程是联系在一起的，因此杀死或暂停一个过程，那么另一个过程也执行相同的过程。虽然这种模式在大多数情况下工作的很好，但有时可以直接启动运行应用程序的进程，而不是间接进行。这就由“Capsule Trampoline”支持。

> 从本质上讲，这个概念定义了当你执行构建的Capsule jar时，它只需输出（以文本形式）运行应用程序所需的完整命令（这将是一个长命令，其中定义了所有的jvm和classpath参数）。然后，这个想法就是复制/粘贴命令并将其原始执行。

> 如果你想构建‘trampoline’可执行capsule，你可以将```<trampoline>true</trampoline>```标志添加到插件的配置中：

```
<configuration>
	<appClass>hello.HelloWorld</appClass>
	<trampoline>true</trampoline>
</configuration>
```
> This will build .tx files like so:

```
target/my-app-1.0-cap.jar
target/my-app-1.0-cap.tx
```
> Which you can run:

```
./target/my-app-1.0-cap.tx
```
> 这将输出你手动复制并粘贴并自行运行的命令，从而确保你的应用只有一个进程。

### 提供应用系统属性（Providing your app System Properties）
> Capsule还支持为您的应用程序提供系统属性。这可以在运行时完成，但它也很方便在构建时定义一些属性。

> 只需在插件配置中添加<properties>标签并声明任何属性即可。

```
<configuration>
	<appClass>hello.HelloWorld</appClass>
	<properties>
		<property>
			<key>boo</key>
			<value>ya</value>
		</property>
	</properties>
</configuration>
```
> 然后，在您的应用代码中的任何位置调用此系统属性：

```
package hello;
public class HelloWorld {
	public static void main(String[] args) {
		System.out.println(System.getProperty("boo")); // outputs 'ya'
	}
}
```

### Additional Manifest Entries
> Capsule支持许多清单条目，将您的应用程序配置为您心中的内容。 请参阅此处的完整参考。

> 因此，例如，如果您想设置JVM-Args：

```
<configuration>
	<appClass>hello.HelloWorld</appClass>
	<manifest>
		<entry>
			<key>JVM-Args</key>
			<value>-Xmx512m</value>
		</entry>
	</manifest>
</configuration>
```
> 请注意，您不需要Main-Class，Application-Class，Application，Dependencies和System-Properties，因为它们是由插件自动生成的。

### 自定义文件名称（Custom File Name）
> 输出capsule jar的名称是根据默认情况下添加了 ```-capsule```的```<finalName>```来标记。基本上这是<finalName>-capsule.jar,例如你的应用可能是app-capsule.jar.

> 如果你想要自定义，那么你可以选择设置构成样式的参数```fileName```和 ```fileDesc```:

```
fileName><fileDesc>.jar
```
> 例如，如果你想让你的输出capsule jar 像'my-amazing-app-cap.jar'那么你需要做以下的事情：

```
<build>
  <plugins>
    <plugin>
      <groupId>com.github.chrisdchristo</groupId>
      <artifactId>capsule-maven-plugin</artifactId>
      <version>${capsule.maven.plugin.version}</version>
      <configuration>
        <appClass>hello.HelloWorld</appClass>
        <fileName>my-amazing-app</fileName>
        <fileDesc>-cap</fileDesc>
      </configuration>
    </plugin>
  </plugins>
</build>

```

### 模式（Modes）
> Capsule支持模式的概念，这实质上意味着根据特定的特性以不同的方式定义您的应用程序jar。您可以通过为每种模式设置特定的清单或系统属性来为应用程序定义不同的模式。因此，例如，您可以由一个测试模式来定义测试数据库连接，同样也可以有一个生产模式来定义生产数据库连接。然后，您可以通过在命令行中添加 ```-Dcapsule.mode=MODE```参数，以特定模式轻松运行capsule。在capsule模式下查看更多信息。

> maven插件支持定义capsule模式的便捷方式（在```<configuration>标签中包含以下内容）：

```
<modes>
            <mode>
              <name>dev</name>
              <manifest>
                <entry>
                  <key>JVM-Args</key>
                  <value>-Xmx512m</value>
                </entry>
              </manifest>
            </mode>
            <mode>
              <name>test</name>
              <manifest>
                <entry>
                  <key>JVM-Args</key>
                  <value>-Xmx512m -Xms512m -Xmn350m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintGCDetails -XX:CMSFullGCsBeforeCompaction=5 -XX:+HeapDumpOnOutOfMemoryError</value>
                </entry>
              </manifest>
            </mode>
            <mode>
              <name>pre</name>
              <manifest>
                <entry>
                  <key>JVM-Args</key>
                  <value>-Xmx512m -Xms512m -Xmn350m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintGCDetails -XX:CMSFullGCsBeforeCompaction=5 -XX:+HeapDumpOnOutOfMemoryError</value>
                </entry>
              </manifest>
            </mode>
            <mode>
              <name>prod</name>
              <manifest>
                <entry>
                  <key>JVM-Args</key>
                  <value>-Xmx2500m -Xms2500m -Xmn1500m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintGCDetails -XX:CMSFullGCsBeforeCompaction=5 -XX:+HeapDumpOnOutOfMemoryError</value>
                </entry>
              </manifest>
            </mode>
          </modes>
```
> 一个模式必须有```<name>```标签，并且您可以为每个模式定义两件事情，即```<properties>```和```<manifest>```(与上述语法完全相同)

> 如果模式在运行时激活（```-Dcapsule.mode=dev```）,则模式中列出的属性将完全覆盖主配置中设置的属性。因此，只有模式部分中列出的属性才可用于应用程序。

> 但是，模式的manifest entries将被附加到主要部分中定义的现有的manifest entries（除非匹配，否则模式的条目将被覆盖）

> 当然，你可以定义多种模式（如上面配置就定义四种，dev，test，pre，prod）

### 文件集（FileSets）
> 如果你想从某个本地文件夹复制特定文件，则可以使用```<configuration>```标记中的assembly样式```<fileSets>```

```
<fileSets>
            <fileSet>
              <directory>config/</directory>
              <outputDirectory>config/</outputDirectory>
              <includes>
                <include>myconfig.yml</include>
              </includes>
            </fileSet>
          </fileSets>
```
> 因此，从上面我们复制了我们在config文件夹中的myconfig.yml文件，并将其放置在capsule jar中的config目录中（插件将在胶囊jar中创建此文件夹）。

> 您可以指定一些```<fileSet>```，它必须包含```<directory>```（要复制的文件夹的位置），```<outputDirectory>```（capsule jar内的目标目录）以及最后一组```<include>```来指定哪些文件 从```<directory>```复制。

> ```<include>```标签支持单个通配符*。 因此，例如```<include> *.yml </ include>```，```<include> myconfig * </ include>```或```<include> my * .yml </ include>```都可以工作。

### DependencySets
> 如果您想从嵌入在某个依赖项中的文件复制特定文件，则可以在<configuration>标记中使用组装样式<dependencySets>。

```
<dependencySets>
	<dependencySet>
		<groupId>com.google.guava</groupId>
		<artifactId>guava</artifactId>
		<outputDirectory>config/</outputDirectory>
		<includes>
			<include>META-INF/MANIFEST.MF</include>
		</includes>
	</dependencySet>
</dependencySets>
```
> 因此，从上面我们拉出来自Google Guava的jar中的清单文件，并将其放置在capsule jar的config目录中（插件将在capsule jar中创建该文件夹）。

> 您可以指定一些```<dependencySet>```，它必须包含项目依赖关系（分类器和版本是可选的）的coords（```<groupdId>```，```<artifactId>```，```<classifier>```，```<version>```），```<outputDirectory>```（目标 目录），最后是一组```<include>```来指定哪些文件来自依赖关系进行复制。

> ```<include>```标签支持单个通配符*。 因此，例如```<include> META-INF / * </ include>```，```<include> * MANIFEST.MF </ include>```或```<include> META-INF / *。MF </ include>```可以工作。

> 如果省略包含标记，则也可以直接复制整个依赖项：

```
<dependencySets>
	<dependencySet>
		<groupId>com.google.guava</groupId>
		<artifactId>guava</artifactId>
		<outputDirectory>config/</outputDirectory>
	</dependencySet>
</dependencySets>
```
> 如果您标记```<unpack> true </ unpack>```标志，您也可以以解压后的形式复制整个依赖项

```
<dependencySets>
	<dependencySet>
		<groupId>com.google.guava</groupId>
		<artifactId>guava</artifactId>
		<outputDirectory>config/</outputDirectory>
		<unpack>true</unpack>
	</dependencySet>
</dependencySets>
```

### Custom Capsule Version
> 该插件可以支持较老或较新版本的capsule（由您自担风险）。 您可以为capsule 版本指定一个maven属性（这将是capsule 版本中capsule的版本）。

```
<properties>
	<capsule.version>0.6.0</capsule.version>
</properties>
```
> 否则，胶囊的默认版本将被自动使用。 这是推荐的。

### Caplets
> Capsule支持通过扩展Capsule.class来定义自己的Capsule类。 如果您想指定自定义Capsule类，请添加指向它的清单条目：

```
<configuration>
	<appClass>hello.HelloWorld</appClass>
	<caplets>MyCapsule</caplets>
</configuration>
```

> 如果您有多个，请在每个之间添加一个空格，例如<caplets> MyCapsule MyCapsule2 </ caplets>。

> 如果你想使用不是本地类的caplet（即从依赖），那么你必须指定它的完整坐标，如下所示：

```
<caplets>co.paralleluniverse:capsule-daemon:0.1.0</caplets>
```

> And you can mix local and non-local caplets too:

```
<caplets>MyCapsule co.paralleluniverse:capsule-daemon:0.1.0</caplets>
```

### Maven Exec Plugin Integration
> he maven exec plugin is a useful tool to run your jar all from within maven (using its classpath).

```
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>exec-maven-plugin</artifactId>
	<version>${maven.exec.plugin.version}</version>
	<configuration>
		<mainClass>hello.HelloWorld</mainClass>
	</configuration>
</plugin>
```
> You can then run your normal jar by:

```
mvn package exec:java
```

> 请注意，exec插件提供了一个配置，您可以在其中指定<mainClass>以及其他字段（如<systemProperties>）。 Capsule插件提供了拉取该配置并将其应用到内置capsule的功能，从而使您无需输入两次（一次在exec插件，另一次在capsule插件处）。

> 在capsule插件中，您可以设置<execPluginConfig>标记来执行此操作：

```
<plugin>
	<groupId>com.github.chrisdchristo</groupId>
	<artifactId>capsule-maven-plugin</artifactId>
	<version>${capsule.maven.plugin.version}</version>
	<configuration>
		<execPluginConfig>root</execPluginConfig>
	</configuration>
</plugin>
```
> ```root```值将告诉胶囊插件从exec插件根目录下的```<configuration>```元素拉取配置。

> 如果您在exec插件中使用执行，如下所示：

```
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>exec-maven-plugin</artifactId>
	<version>${maven.exec.plugin.version}</version>
	<executions>
		<execution>
			<id>default-cli</id>
			<goals>
				<goal>java</goal>
			</goals>
			<configuration>
				<mainClass>hello.HelloWorld</mainClass>
			</configuration>
		</execution>
	</executions>
</plugin>
```
> 然后你可以指定执行的ID为<execPluginConfig>：

```
<plugin>
	<groupId>com.github.chrisdchristo</groupId>
	<artifactId>capsule-maven-plugin</artifactId>
	<version>${capsule.maven.plugin.version}</version>
	<configuration>
		<execPluginConfig>default-cli</execPluginConfig>
	</configuration>
</plugin>
```

###### capsule插件如何映射exec插件的配置
> capsule 插件将映射来自exec插件的值：

```
mainClass -> appClass
systemProperties -> properties
arguments -> JVM-Args (manifest entry)
```

###### 完整的解决方案

```
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>exec-maven-plugin</artifactId>
	<version>${maven.exec.plugin.version}</version>
	<configuration>
		<mainClass>hello.HelloWorld</mainClass>
		<systemProperties>
			<property>
				<key>propertyName1</key>
				<value>propertyValue1</value>
			</property>
		</systemProperties>
	</configuration>
</plugin>
<plugin>
	<groupId>com.github.chrisdchristo</groupId>
	<artifactId>capsule-maven-plugin</artifactId>
	<version>${capsule.maven.plugin.version}</version>
	<configuration>
		<execPluginConfig>root</execPluginConfig>
	</configuration>
</plugin>
```
###### 覆盖exec插件配置
> 请注意，如果您确实指定了capsule插件的<appClass>，<properties>或JVM-Args（在<manifest>中），那么这些将覆盖exec插件的配置。


### Refence

```
<!-- BUILD CAPSULES -->
<plugin>
	<groupId>com.github.chrisdchristo</groupId>
	<artifactId>capsule-maven-plugin</artifactId>
	<version>${capsule.maven.plugin.version}</version>
	<configuration>

		<appClass>hello.HelloWorld</appClass>

		<outputDir>target</outputDir>
		<caplets>MyCapsule MyCapsule2</caplets>

		<type>fat</type>
		<chmod>true</chmod>
		<trampoline>true</trampoline>
		<setManifestRepos>true</setManifestRepos>

		<includeApp>true</includeApp>
		<includeAppDep>false</includeAppDep>
		<includePluginDep>false</includePluginDep>
		<includeTransitiveDep>false</includeTransitiveDep>
		<includeCompileDep>false</includeCompileDep>
		<includeRuntimeDep>false</includeRuntimeDep>
		<includeProvidedDep>false</includeProvidedDep>
		<includeSystemDep>false</includeSystemDep>
		<includeTestDep>false</includeTestDep>
		<includeOptionalDep>false</includeOptionalDep>

		<resolveApp>false</resolveApp>
		<resolveAppDep>false</resolveAppDep>
		<resolvePluginDep>false</resolvePluginDep>
		<resolveTransitiveDep>false</resolveTransitiveDep>
		<resolveCompileDep>false</resolveCompileDep>
		<resolveRuntimeDep>false</resolveRuntimeDep>
		<resolveProvidedDep>false</resolveProvidedDep>
		<resolveSystemDep>false</resolveSystemDep>
		<resolveTestDep>false</resolveTestDep>
		<resolveOptionalDep>false</resolveOptionalDep>

		<execPluginConfig>root</execPluginConfig>
		<fileName>my-amazing-app</fileName>
		<fileDesc>-cap</fileDesc>

		<properties>
			<property>
				<key>propertyName1</key>
				<value>propertyValue1</value>
			</property>
		</properties>

		<manifest>
			<entry>
				<key>JVM-Args</key>
				<value>-Xmx512m</value>
			</entry>
			<entry>
				<key>Min-Java-Version</key>
				<value>1.8.0</value>
			</entry>
		</manifest>

		<modes>
			<mode>
				<name>production</name>
				<properties>
					<property>
						<key>dbConnectionServer</key>
						<value>aws.amazon.example</value>
					</property>
				</properties>
				<manifest>
					<entry>
						<key>JVM-Args</key>
						<value>-Xmx1024m</value>
					</entry>
				</manifest>
			</mode>
		</modes>

		<fileSets>
			<fileSet>
				<directory>config/</directory>
				<outputDirectory>config/</outputDirectory>
				<includes>
					<include>myconfig.yml</include>
				</includes>
			</fileSet>
		</fileSets>

		<dependencySets>
			<dependencySet>
			  <groupId>com.google.guava</groupId>
			  <artifactId>guava</artifactId>
			  <version>optional</version>
			  <outputDirectory>config/</outputDirectory>
			  <!--<unpack>true</unpack>-->
			  <includes>
			    <include>META-INF/MANIFEST.MF</include>
			  </includes>
			</dependencySet>
		</dependencySets>

	</configuration>
	<executions>
		<execution>
			<goals>
				<goal>build</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```



