begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|LockReleaseFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NativeFSLockFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|optional
operator|.
name|junit
operator|.
name|JUnitResultFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|optional
operator|.
name|junit
operator|.
name|JUnitTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|optional
operator|.
name|junit
operator|.
name|JUnitTestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|util
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Just like BriefJUnitResultFormatter "brief" bundled with ant,  * except all formatted text is buffered until the test suite is finished.  * At this point, the output is written at once in synchronized fashion.  * This way tests can run in parallel without interleaving output.  */
end_comment

begin_class
DECL|class|LuceneJUnitResultFormatter
specifier|public
class|class
name|LuceneJUnitResultFormatter
implements|implements
name|JUnitResultFormatter
block|{
DECL|field|ONE_SECOND
specifier|private
specifier|static
specifier|final
name|double
name|ONE_SECOND
init|=
literal|1000.0
decl_stmt|;
DECL|field|lockFactory
specifier|private
specifier|static
specifier|final
name|NativeFSLockFactory
name|lockFactory
decl_stmt|;
comment|/** Where to write the log to. */
DECL|field|out
specifier|private
name|OutputStream
name|out
decl_stmt|;
comment|/** Formatter for timings. */
DECL|field|numberFormat
specifier|private
name|NumberFormat
name|numberFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|/** Output suite has written to System.out */
DECL|field|systemOutput
specifier|private
name|String
name|systemOutput
init|=
literal|null
decl_stmt|;
comment|/** Output suite has written to System.err */
DECL|field|systemError
specifier|private
name|String
name|systemError
init|=
literal|null
decl_stmt|;
comment|/** Buffer output until the end of the test */
DECL|field|sb
specifier|private
name|ByteArrayOutputStream
name|sb
decl_stmt|;
comment|// use a BOS for our mostly ascii-output
DECL|field|lock
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Lock
name|lock
decl_stmt|;
static|static
block|{
name|File
name|lockDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|,
literal|"lucene_junit_lock"
argument_list|)
decl_stmt|;
name|lockDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not make Lock directory:"
operator|+
name|lockDir
argument_list|)
throw|;
block|}
try|try
block|{
name|lockFactory
operator|=
operator|new
name|NativeFSLockFactory
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
name|lock
operator|=
name|lockFactory
operator|.
name|makeLock
argument_list|(
literal|"junit_lock"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Constructor for LuceneJUnitResultFormatter. */
DECL|method|LuceneJUnitResultFormatter
specifier|public
name|LuceneJUnitResultFormatter
parameter_list|()
block|{   }
comment|/**    * Sets the stream the formatter is supposed to write its results to.    * @param out the output stream to write to    */
DECL|method|setOutput
specifier|public
name|void
name|setOutput
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * @see JUnitResultFormatter#setSystemOutput(String)    */
comment|/** {@inheritDoc}. */
DECL|method|setSystemOutput
specifier|public
name|void
name|setSystemOutput
parameter_list|(
name|String
name|out
parameter_list|)
block|{
name|systemOutput
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * @see JUnitResultFormatter#setSystemError(String)    */
comment|/** {@inheritDoc}. */
DECL|method|setSystemError
specifier|public
name|void
name|setSystemError
parameter_list|(
name|String
name|err
parameter_list|)
block|{
name|systemError
operator|=
name|err
expr_stmt|;
block|}
comment|/**    * The whole testsuite started.    * @param suite the test suite    */
DECL|method|startTestSuite
specifier|public
specifier|synchronized
name|void
name|startTestSuite
parameter_list|(
name|JUnitTest
name|suite
parameter_list|)
block|{
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
return|return;
comment|// Quick return - no output do nothing.
block|}
name|sb
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
comment|// don't reuse, so its gc'ed
try|try
block|{
name|LogManager
operator|.
name|getLogManager
argument_list|()
operator|.
name|readConfiguration
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|append
argument_list|(
literal|"Testsuite: "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|suite
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
block|}
comment|/**    * The whole testsuite ended.    * @param suite the test suite    */
DECL|method|endTestSuite
specifier|public
specifier|synchronized
name|void
name|endTestSuite
parameter_list|(
name|JUnitTest
name|suite
parameter_list|)
block|{
name|append
argument_list|(
literal|"Tests run: "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|suite
operator|.
name|runCount
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
literal|", Failures: "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|suite
operator|.
name|failureCount
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
literal|", Errors: "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|suite
operator|.
name|errorCount
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
literal|", Time elapsed: "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|numberFormat
operator|.
name|format
argument_list|(
name|suite
operator|.
name|getRunTime
argument_list|()
operator|/
name|ONE_SECOND
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
literal|" sec"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
comment|// append the err and output streams to the log
if|if
condition|(
name|systemOutput
operator|!=
literal|null
operator|&&
name|systemOutput
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|append
argument_list|(
literal|"------------- Standard Output ---------------"
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|systemOutput
argument_list|)
operator|.
name|append
argument_list|(
literal|"------------- ---------------- ---------------"
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|systemError
operator|!=
literal|null
operator|&&
name|systemError
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|append
argument_list|(
literal|"------------- Standard Error -----------------"
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|systemError
argument_list|)
operator|.
name|append
argument_list|(
literal|"------------- ---------------- ---------------"
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|obtain
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
try|try
block|{
name|sb
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockReleaseFailedException
name|e
parameter_list|)
block|{
comment|// well lets pretend its released anyway
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to write results"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
name|System
operator|.
name|out
operator|&&
name|out
operator|!=
name|System
operator|.
name|err
condition|)
block|{
name|FileUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * A test started.    * @param test a test    */
DECL|method|startTest
specifier|public
name|void
name|startTest
parameter_list|(
name|Test
name|test
parameter_list|)
block|{   }
comment|/**    * A test ended.    * @param test a test    */
DECL|method|endTest
specifier|public
name|void
name|endTest
parameter_list|(
name|Test
name|test
parameter_list|)
block|{   }
comment|/**    * Interface TestListener for JUnit&lt;= 3.4.    *    *<p>A Test failed.    * @param test a test    * @param t    the exception thrown by the test    */
DECL|method|addFailure
specifier|public
name|void
name|addFailure
parameter_list|(
name|Test
name|test
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|formatError
argument_list|(
literal|"\tFAILED"
argument_list|,
name|test
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    * Interface TestListener for JUnit&gt; 3.4.    *    *<p>A Test failed.    * @param test a test    * @param t    the assertion failed by the test    */
DECL|method|addFailure
specifier|public
name|void
name|addFailure
parameter_list|(
name|Test
name|test
parameter_list|,
name|AssertionFailedError
name|t
parameter_list|)
block|{
name|addFailure
argument_list|(
name|test
argument_list|,
operator|(
name|Throwable
operator|)
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    * A test caused an error.    * @param test  a test    * @param error the error thrown by the test    */
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|Test
name|test
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
name|formatError
argument_list|(
literal|"\tCaused an ERROR"
argument_list|,
name|test
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**    * Format the test for printing..    * @param test a test    * @return the formatted testname    */
DECL|method|formatTest
specifier|protected
name|String
name|formatTest
parameter_list|(
name|Test
name|test
parameter_list|)
block|{
if|if
condition|(
name|test
operator|==
literal|null
condition|)
block|{
return|return
literal|"Null Test: "
return|;
block|}
else|else
block|{
return|return
literal|"Testcase: "
operator|+
name|test
operator|.
name|toString
argument_list|()
operator|+
literal|":"
return|;
block|}
block|}
comment|/**    * Format an error and print it.    * @param type the type of error    * @param test the test that failed    * @param error the exception that the test threw    */
DECL|method|formatError
specifier|protected
specifier|synchronized
name|void
name|formatError
parameter_list|(
name|String
name|type
parameter_list|,
name|Test
name|test
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
name|test
operator|!=
literal|null
condition|)
block|{
name|endTest
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|append
argument_list|(
name|formatTest
argument_list|(
name|test
argument_list|)
operator|+
name|type
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
name|String
name|strace
init|=
name|JUnitTestRunner
operator|.
name|getFilteredTrace
argument_list|(
name|error
argument_list|)
decl_stmt|;
name|append
argument_list|(
name|strace
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|StringUtils
operator|.
name|LINE_SEP
argument_list|)
expr_stmt|;
block|}
DECL|method|append
specifier|public
name|LuceneJUnitResultFormatter
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
try|try
block|{
name|sb
operator|.
name|write
argument_list|(
name|s
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// intentionally use default charset, its a console.
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
DECL|method|append
specifier|public
name|LuceneJUnitResultFormatter
name|append
parameter_list|(
name|long
name|l
parameter_list|)
block|{
return|return
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|l
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

