begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|nio
operator|.
name|file
operator|.
name|FileVisitResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|SimpleFileVisitor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|BasicFileAttributes
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
name|Format
import|;
end_import

begin_comment
comment|/**  * Base class for source of data for benchmarking  *<p>  * Keeps track of various statistics, such as how many data items were generated,   * size in bytes etc.  *<p>  * Supports the following configuration parameters:  *<ul>  *<li><b>content.source.forever</b> - specifies whether to generate items  * forever (<b>default=true</b>).  *<li><b>content.source.verbose</b> - specifies whether messages should be  * output by the content source (<b>default=false</b>).  *<li><b>content.source.encoding</b> - specifies which encoding to use when  * reading the files of that content source. Certain implementations may define  * a default value if this parameter is not specified. (<b>default=null</b>).  *<li><b>content.source.log.step</b> - specifies for how many items a  * message should be logged. If set to 0 it means no logging should occur.  *<b>NOTE:</b> if verbose is set to false, logging should not occur even if  * logStep is not 0 (<b>default=0</b>).  *</ul>  */
end_comment

begin_class
DECL|class|ContentItemsSource
specifier|public
specifier|abstract
class|class
name|ContentItemsSource
implements|implements
name|Closeable
block|{
DECL|field|bytesCount
specifier|private
name|long
name|bytesCount
decl_stmt|;
DECL|field|totalBytesCount
specifier|private
name|long
name|totalBytesCount
decl_stmt|;
DECL|field|itemCount
specifier|private
name|int
name|itemCount
decl_stmt|;
DECL|field|totalItemCount
specifier|private
name|int
name|totalItemCount
decl_stmt|;
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
DECL|field|lastPrintedNumUniqueTexts
specifier|private
name|int
name|lastPrintedNumUniqueTexts
init|=
literal|0
decl_stmt|;
DECL|field|lastPrintedNumUniqueBytes
specifier|private
name|long
name|lastPrintedNumUniqueBytes
init|=
literal|0
decl_stmt|;
DECL|field|printNum
specifier|private
name|int
name|printNum
init|=
literal|0
decl_stmt|;
DECL|field|forever
specifier|protected
name|boolean
name|forever
decl_stmt|;
DECL|field|logStep
specifier|protected
name|int
name|logStep
decl_stmt|;
DECL|field|verbose
specifier|protected
name|boolean
name|verbose
decl_stmt|;
DECL|field|encoding
specifier|protected
name|String
name|encoding
decl_stmt|;
comment|/** update count of bytes generated by this source */
DECL|method|addBytes
specifier|protected
specifier|final
specifier|synchronized
name|void
name|addBytes
parameter_list|(
name|long
name|numBytes
parameter_list|)
block|{
name|bytesCount
operator|+=
name|numBytes
expr_stmt|;
name|totalBytesCount
operator|+=
name|numBytes
expr_stmt|;
block|}
comment|/** update count of items generated by this source */
DECL|method|addItem
specifier|protected
specifier|final
specifier|synchronized
name|void
name|addItem
parameter_list|()
block|{
operator|++
name|itemCount
expr_stmt|;
operator|++
name|totalItemCount
expr_stmt|;
block|}
comment|/**    * A convenience method for collecting all the files of a content source from    * a given directory. The collected {@link Path} instances are stored in the    * given<code>files</code>.    */
DECL|method|collectFiles
specifier|protected
specifier|final
name|void
name|collectFiles
parameter_list|(
name|Path
name|dir
parameter_list|,
specifier|final
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|dir
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|file
operator|.
name|toRealPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true whether it's time to log a message (depending on verbose and    * the number of items generated).    */
DECL|method|shouldLog
specifier|protected
specifier|final
name|boolean
name|shouldLog
parameter_list|()
block|{
return|return
name|verbose
operator|&&
name|logStep
operator|>
literal|0
operator|&&
name|itemCount
operator|%
name|logStep
operator|==
literal|0
return|;
block|}
comment|/** Called when reading from this content source is no longer required. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of bytes generated since last reset. */
DECL|method|getBytesCount
specifier|public
specifier|final
name|long
name|getBytesCount
parameter_list|()
block|{
return|return
name|bytesCount
return|;
block|}
comment|/** Returns the number of generated items since last reset. */
DECL|method|getItemsCount
specifier|public
specifier|final
name|int
name|getItemsCount
parameter_list|()
block|{
return|return
name|itemCount
return|;
block|}
DECL|method|getConfig
specifier|public
specifier|final
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** Returns the total number of bytes that were generated by this source. */
DECL|method|getTotalBytesCount
specifier|public
specifier|final
name|long
name|getTotalBytesCount
parameter_list|()
block|{
return|return
name|totalBytesCount
return|;
block|}
comment|/** Returns the total number of generated items. */
DECL|method|getTotalItemsCount
specifier|public
specifier|final
name|int
name|getTotalItemsCount
parameter_list|()
block|{
return|return
name|totalItemCount
return|;
block|}
comment|/**    * Resets the input for this content source, so that the test would behave as    * if it was just started, input-wise.    *<p>    *<b>NOTE:</b> the default implementation resets the number of bytes and    * items generated since the last reset, so it's important to call    * super.resetInputs in case you override this method.    */
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|bytesCount
operator|=
literal|0
expr_stmt|;
name|itemCount
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Sets the {@link Config} for this content source. If you override this    * method, you must call super.setConfig.    */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|forever
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.forever"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|logStep
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.log.step"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verbose
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.verbose"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|encoding
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.encoding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|printStatistics
specifier|public
name|void
name|printStatistics
parameter_list|(
name|String
name|itemsName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|verbose
condition|)
block|{
return|return;
block|}
name|boolean
name|print
init|=
literal|false
decl_stmt|;
name|String
name|col
init|=
literal|"                  "
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
name|sb
operator|.
name|append
argument_list|(
literal|"------------> "
argument_list|)
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" statistics ("
argument_list|)
operator|.
name|append
argument_list|(
name|printNum
argument_list|)
operator|.
name|append
argument_list|(
literal|"): "
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|int
name|nut
init|=
name|getTotalItemsCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|nut
operator|>
name|lastPrintedNumUniqueTexts
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total count of "
operator|+
name|itemsName
operator|+
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|nut
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|lastPrintedNumUniqueTexts
operator|=
name|nut
expr_stmt|;
block|}
name|long
name|nub
init|=
name|getTotalBytesCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|nub
operator|>
name|lastPrintedNumUniqueBytes
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total bytes of "
operator|+
name|itemsName
operator|+
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|nub
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|lastPrintedNumUniqueBytes
operator|=
name|nub
expr_stmt|;
block|}
if|if
condition|(
name|getItemsCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"num "
operator|+
name|itemsName
operator|+
literal|" added since last inputs reset:   "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|getItemsCount
argument_list|()
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total bytes added for "
operator|+
name|itemsName
operator|+
literal|" since last inputs reset: "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|getBytesCount
argument_list|()
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|print
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|printNum
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

