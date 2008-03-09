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
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|FileInputStream
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
name|InputStreamReader
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
name|ParseException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * A DocMaker using the (compressed) Trec collection for its input.  *<p>  * Config properties:<ul>  *<li>work.dir=&lt;path to the root of docs and indexes dirs| Default: work&gt;</li>  *<li>docs.dir=&lt;path to the docs dir| Default: trec&gt;</li>  *</ul>  */
end_comment

begin_class
DECL|class|TrecDocMaker
specifier|public
class|class
name|TrecDocMaker
extends|extends
name|BasicDocMaker
block|{
DECL|field|newline
specifier|private
specifier|static
specifier|final
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|dateFormat
specifier|protected
name|ThreadLocal
name|dateFormat
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|dataDir
specifier|protected
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|inputFiles
specifier|protected
name|ArrayList
name|inputFiles
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|nextFile
specifier|protected
name|int
name|nextFile
init|=
literal|0
decl_stmt|;
DECL|field|iteration
specifier|protected
name|int
name|iteration
init|=
literal|0
decl_stmt|;
DECL|field|reader
specifier|protected
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|zis
specifier|private
name|GZIPInputStream
name|zis
decl_stmt|;
DECL|field|DATE_FORMATS
specifier|private
specifier|static
specifier|final
name|String
name|DATE_FORMATS
index|[]
init|=
block|{
literal|"EEE, dd MMM yyyy kk:mm:ss z"
block|,
comment|//Tue, 09 Dec 2003 22:39:08 GMT
literal|"EEE MMM dd kk:mm:ss yyyy z"
block|,
comment|//Tue Dec 09 16:45:08 2003 EST
literal|"EEE, dd-MMM-':'y kk:mm:ss z"
block|,
comment|//Tue, 09 Dec 2003 22:39:08 GMT
literal|"EEE, dd-MMM-yyy kk:mm:ss z"
block|,
comment|//Tue, 09 Dec 2003 22:39:08 GMT
block|}
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
literal|"trec"
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
DECL|method|openNextFile
specifier|protected
name|void
name|openNextFile
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|Exception
block|{
name|closeInputs
argument_list|()
expr_stmt|;
name|int
name|retries
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|File
name|f
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
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"opening: "
operator|+
name|f
operator|+
literal|" length: "
operator|+
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|zis
operator|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|zis
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|retries
operator|++
expr_stmt|;
if|if
condition|(
name|retries
operator|<
literal|20
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skipping 'bad' file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"  #retries="
operator|+
name|retries
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
DECL|method|closeInputs
specifier|protected
name|void
name|closeInputs
parameter_list|()
block|{
if|if
condition|(
name|zis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|zis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"closeInputs(): Ingnoring error: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|zis
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"closeInputs(): Ingnoring error: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// read until finding a line that starts with the specified prefix
DECL|method|read
specifier|protected
name|StringBuffer
name|read
parameter_list|(
name|String
name|prefix
parameter_list|,
name|StringBuffer
name|sb
parameter_list|,
name|boolean
name|collectMatchLine
parameter_list|,
name|boolean
name|collectAll
parameter_list|)
throws|throws
name|Exception
block|{
name|sb
operator|=
operator|(
name|sb
operator|==
literal|null
condition|?
operator|new
name|StringBuffer
argument_list|()
else|:
name|sb
operator|)
expr_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
if|if
condition|(
name|collectMatchLine
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|collectAll
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
block|}
comment|//System.out.println("read: "+sb);
return|return
name|sb
return|;
block|}
DECL|method|getNextDocData
specifier|protected
specifier|synchronized
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|Exception
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
block|}
comment|// 1. skip until doc start
name|read
argument_list|(
literal|"<DOC>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 2. name
name|StringBuffer
name|sb
init|=
name|read
argument_list|(
literal|"<DOCNO>"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|"<DOCNO>"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|indexOf
argument_list|(
literal|"</DOCNO>"
argument_list|)
argument_list|)
operator|+
literal|"_"
operator|+
name|iteration
expr_stmt|;
comment|// 3. skip until doc header
name|read
argument_list|(
literal|"<DOCHDR>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 4. date
name|sb
operator|=
name|read
argument_list|(
literal|"Date: "
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|dateStr
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|"Date: "
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// 5. skip until end of doc header
name|read
argument_list|(
literal|"</DOCHDR>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 6. collect until end of doc
name|sb
operator|=
name|read
argument_list|(
literal|"</DOC>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// this is the next document, so parse it
name|Date
name|date
init|=
name|parseDate
argument_list|(
name|dateStr
argument_list|)
decl_stmt|;
name|HTMLParser
name|p
init|=
name|getHtmlParser
argument_list|()
decl_stmt|;
name|DocData
name|docData
init|=
name|p
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|date
argument_list|,
name|sb
argument_list|,
name|getDateFormat
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|addBytes
argument_list|(
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// count char length of parsed html text (larger than the plain doc body text).
return|return
name|docData
return|;
block|}
DECL|method|getDateFormat
specifier|protected
name|DateFormat
name|getDateFormat
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|DateFormat
name|df
index|[]
init|=
operator|(
name|DateFormat
index|[]
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
name|df
operator|=
operator|new
name|SimpleDateFormat
index|[
name|DATE_FORMATS
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|df
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|df
index|[
name|i
index|]
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FORMATS
index|[
name|i
index|]
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|df
index|[
name|i
index|]
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
index|[
name|n
index|]
return|;
block|}
DECL|method|parseDate
specifier|protected
name|Date
name|parseDate
parameter_list|(
name|String
name|dateStr
parameter_list|)
block|{
name|Date
name|date
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DATE_FORMATS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|date
operator|=
name|getDateFormat
argument_list|(
name|i
argument_list|)
operator|.
name|parse
argument_list|(
name|dateStr
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|date
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{       }
block|}
comment|// do not fail test just because a date could not be parsed
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ignoring date parse exception (assigning 'now') for: "
operator|+
name|dateStr
argument_list|)
expr_stmt|;
name|date
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
comment|// now
return|return
name|date
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
name|closeInputs
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

