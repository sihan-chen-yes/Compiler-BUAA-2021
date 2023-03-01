# Compiler-BUAA-2021
编译实验代码 最终竞速排名在24/250（6个testfile取平均） master分支下为最终提交至排行榜的版本（做完优化的最终版本）,finForTest分支下为参加期末考试的版本（无优化的最终版本）

- Built a compiler using Java, which can transform SysY (a subset of C) from source code to MIPS assembly code
- Included modules of Lexical Analysis (Automata Theory-based), Syntax Analysis (Recursive Descent-based), Semantic Analysis (Abstract Syntax Tree-based), Middle Code Generation (Quaternary Formula-based), Target Code Generation (MIPS-based)
- Optimized the compiler via Inline Function, Loop Optimization, Register Allocation Optimization, among others; ranked 24 / 250 finally

`\Java\compiler\out\artifacts\compiler_jar\compiler.jar`为项目的jar包

在命令行中输入`java -jar compiler.jar`即可进行编译，编译后会输出以下文件：

* **error.txt**:编译过程中的异常
* **midcode.txt**:编译产生的中间代码(四元式)
* **mips.txt**:编译产生的目标代码(MIPS)

* **opmidcode.txt**:优化后的中间代码(四元式)
* **opmips.txt**:优化后的目标代码(MIPS)
* **symbleTable.txt**:

