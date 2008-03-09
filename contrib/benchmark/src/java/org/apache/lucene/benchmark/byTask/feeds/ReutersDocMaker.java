begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * A DocMaker using the Reuters collection for its input.  *<p>  * Config properties:<ul>  *<li>work.dir=&lt;path to the root of docs and indexes dirs| Default: work&gt;</li>  *<li>docs.dir=&lt;path to the docs dir| Default: reuters-out&gt;</li>  *</ul>  */
end_comment

begin_class
DECL|class|ReutersDocMaker
specifier|public
class|class
name|ReutersDocMaker
extends|extends
name|BasicDocMaker
block|{
DECL|field|dateFormat
specifier|private
name|ThreadLocal
name|dateFormat
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|inputFiles
specifier|private
name|ArrayList
name|inputFiles
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|nextFile
specifier|private
name|int
name|nextFile
init|=
literal|0
decl_stmt|;
DECL|field|iteration
specifier|private
name|int
name|iteration
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc)    * @see SimpleDocMaker#setConfig(java.util.Properties)    */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|d
init|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.dir"
argument_list|,
literal|"reuters-out"
argument_list|)
decl_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dataDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|resetUniqueBytes
argument_list|()
expr_stmt|;
name|inputFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|collectFiles
argument_list|(
name|dataDir
argument_list|,
name|inputFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputFiles
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No txt files in dataDir: "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// get/initiate a thread-local simple date format (must do so
comment|// because SimpleDateFormat is not thread-safe.
DECL|method|getDateFormat
specifier|protected
specifier|synchronized
name|DateFormat
name|getDateFormat
parameter_list|()
block|{
name|DateFormat
name|df
init|=
operator|(
name|DateFormat
operator|)
name|dateFormat
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|df
operator|==
literal|null
condition|)
block|{
comment|// date format: 30-MAR-1987 14:22:36.87
name|df
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd-MMM-yyyy kk:mm:ss.SSS"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|df
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dateFormat
operator|.
name|set
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
return|return
name|df
return|;
block|}
DECL|method|getNextDocData
specifier|protected
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|f
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|nextFile
operator|>=
name|inputFiles
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// exhausted files, start a new round, unless forever set to false.
if|if
condition|(
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|++
expr_stmt|;
block|}
name|f
operator|=
operator|(
name|File
operator|)
name|inputFiles
operator|.
name|get
argument_list|(
name|nextFile
operator|++
argument_list|)
expr_stmt|;
name|name
operator|=
name|f
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|"_"
operator|+
name|iteration
expr_stmt|;
block|}
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
comment|//First line is the date, 3rd is the title, rest is body
name|String
name|dateStr
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|//skip an empty line
name|String
name|title
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|//skip an empty line
name|StringBuffer
name|bodyBuf
init|=
operator|new
name|StringBuffer
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|bodyBuf
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|addBytes
argument_list|(
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Date
name|date
init|=
name|getDateFormat
argument_list|()
operator|.
name|parse
argument_list|(
name|dateStr
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocData
argument_list|(
name|name
argument_list|,
name|bodyBuf
operator|.
name|toString
argument_list|()
argument_list|,
name|title
argument_list|,
literal|null
argument_list|,
name|date
argument_list|)
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#resetIinputs()    */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|=
literal|0
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#numUniqueTexts()    */
DECL|method|numUniqueTexts
specifier|public
name|int
name|numUniqueTexts
parameter_list|()
block|{
return|return
name|inputFiles
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

