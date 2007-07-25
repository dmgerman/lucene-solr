begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
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
name|utils
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|Reader
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Perf run configuration properties.  *<p>  * Numeric peroperty containing ":", e.g. "10:100:5" is interpreted   * as array of numeric values. It is extracted once, on first use, and   * maintain a round number to return the appropriate value.  *<p>  * The config property "work.dir" tells where is the root of   * docs data dirs and indexes dirs. It is set to either of:<ul>  *<li>value supplied for it in the alg file;</li>  *<li>otherwise, value of System property "benchmark.work.dir";</li>  *<li>otherwise, "work".</li>  *</ul>  */
end_comment

begin_class
DECL|class|Config
specifier|public
class|class
name|Config
block|{
DECL|field|NEW_LINE
specifier|private
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|roundNumber
specifier|private
name|int
name|roundNumber
init|=
literal|0
decl_stmt|;
DECL|field|props
specifier|private
name|Properties
name|props
decl_stmt|;
DECL|field|valByRound
specifier|private
name|HashMap
name|valByRound
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|colForValByRound
specifier|private
name|HashMap
name|colForValByRound
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|algorithmText
specifier|private
name|String
name|algorithmText
decl_stmt|;
comment|/**    * Read both algorithm and config properties.    * @param algReader from where to read algorithm and config properties.    * @throws IOException    */
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|Reader
name|algReader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read alg file to array of lines
name|ArrayList
name|lines
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
name|algReader
argument_list|)
decl_stmt|;
name|int
name|lastConfigLine
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|line
init|=
name|r
operator|.
name|readLine
argument_list|()
init|;
name|line
operator|!=
literal|null
condition|;
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
control|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
operator|>
literal|0
condition|)
block|{
name|lastConfigLine
operator|=
name|lines
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// copy props lines to string
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|lastConfigLine
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
comment|// read props from string
name|this
operator|.
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure work dir is set properly
if|if
condition|(
name|props
operator|.
name|get
argument_list|(
literal|"work.dir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"work.dir"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"benchmark.work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|printProps
argument_list|()
expr_stmt|;
block|}
comment|// copy algorithm lines
name|sb
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|lastConfigLine
init|;
name|i
operator|<
name|lines
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
name|algorithmText
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create config without algorithm - usefull for a programmatic perf test.    * @param props - configuration properties.    * @throws IOException    */
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
name|this
operator|.
name|props
operator|=
name|props
expr_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|printProps
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printProps
specifier|private
name|void
name|printProps
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> config properties:"
argument_list|)
expr_stmt|;
name|List
name|propKeys
init|=
operator|new
name|ArrayList
argument_list|(
name|props
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|propKeys
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|propKeys
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|propName
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|propName
operator|+
literal|" = "
operator|+
name|props
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a string property.    * @param name name of property.    * @param dflt default value.    * @return a string property.    */
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|dflt
parameter_list|)
block|{
return|return
name|props
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
name|dflt
argument_list|)
return|;
block|}
comment|/**    * Set a property.    * Note: once a multiple values property is set, it can no longer be modified.    * @param name name of property.    * @param value either single or multiple propery value (multple values are separated by ":")    * @throws Exception     */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|valByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot modify a multi value property!"
argument_list|)
throw|;
block|}
name|props
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return an int property.    * If the property contain ":", e.g. "10:100:5", it is interpreted     * as array of ints. It is extracted once, on first call    * to get() it, and a by-round-value is returned.     * @param name name of property    * @param dflt default value    * @return a int property.    */
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|dflt
parameter_list|)
block|{
comment|// use value by round if already parsed
name|int
name|vals
index|[]
init|=
operator|(
name|int
index|[]
operator|)
name|valByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
return|return
name|vals
index|[
name|roundNumber
operator|%
name|vals
operator|.
name|length
index|]
return|;
block|}
comment|// done if not by round
name|String
name|sval
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
literal|""
operator|+
name|dflt
argument_list|)
decl_stmt|;
if|if
condition|(
name|sval
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|sval
argument_list|)
return|;
block|}
comment|// first time this prop is extracted by round
name|int
name|k
init|=
name|sval
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|colName
init|=
name|sval
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|sval
operator|=
name|sval
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
expr_stmt|;
name|colForValByRound
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|colName
argument_list|)
expr_stmt|;
name|vals
operator|=
name|propToIntArray
argument_list|(
name|sval
argument_list|)
expr_stmt|;
name|valByRound
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|vals
argument_list|)
expr_stmt|;
return|return
name|vals
index|[
name|roundNumber
operator|%
name|vals
operator|.
name|length
index|]
return|;
block|}
comment|/**    * Return a boolean property.    * If the property contain ":", e.g. "true.true.false", it is interpreted     * as array of boleans. It is extracted once, on first call    * to get() it, and a by-round-value is returned.     * @param name name of property    * @param dflt default value    * @return a int property.    */
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|dflt
parameter_list|)
block|{
comment|// use value by round if already parsed
name|boolean
name|vals
index|[]
init|=
operator|(
name|boolean
index|[]
operator|)
name|valByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
return|return
name|vals
index|[
name|roundNumber
operator|%
name|vals
operator|.
name|length
index|]
return|;
block|}
comment|// done if not by round
name|String
name|sval
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
literal|""
operator|+
name|dflt
argument_list|)
decl_stmt|;
if|if
condition|(
name|sval
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|sval
argument_list|)
operator|.
name|booleanValue
argument_list|()
return|;
block|}
comment|// first time this prop is extracted by round
name|int
name|k
init|=
name|sval
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|colName
init|=
name|sval
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|sval
operator|=
name|sval
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
expr_stmt|;
name|colForValByRound
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|colName
argument_list|)
expr_stmt|;
name|vals
operator|=
name|propToBooleanArray
argument_list|(
name|sval
argument_list|)
expr_stmt|;
name|valByRound
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|vals
argument_list|)
expr_stmt|;
return|return
name|vals
index|[
name|roundNumber
operator|%
name|vals
operator|.
name|length
index|]
return|;
block|}
comment|/**    * Increment the round number, for config values that are extracted by round number.     * @return the new round number.    */
DECL|method|newRound
specifier|public
name|int
name|newRound
parameter_list|()
block|{
name|roundNumber
operator|++
expr_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"--> Round "
argument_list|)
operator|.
name|append
argument_list|(
name|roundNumber
operator|-
literal|1
argument_list|)
operator|.
name|append
argument_list|(
literal|"-->"
argument_list|)
operator|.
name|append
argument_list|(
name|roundNumber
argument_list|)
decl_stmt|;
comment|// log changes in values
if|if
condition|(
name|valByRound
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|valByRound
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|a
init|=
name|valByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|instanceof
name|int
index|[]
condition|)
block|{
name|int
name|ai
index|[]
init|=
operator|(
name|int
index|[]
operator|)
name|a
decl_stmt|;
name|int
name|n1
init|=
operator|(
name|roundNumber
operator|-
literal|1
operator|)
operator|%
name|ai
operator|.
name|length
decl_stmt|;
name|int
name|n2
init|=
name|roundNumber
operator|%
name|ai
operator|.
name|length
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|ai
index|[
name|n1
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"-->"
argument_list|)
operator|.
name|append
argument_list|(
name|ai
index|[
name|n2
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|ab
index|[]
init|=
operator|(
name|boolean
index|[]
operator|)
name|a
decl_stmt|;
name|int
name|n1
init|=
operator|(
name|roundNumber
operator|-
literal|1
operator|)
operator|%
name|ab
operator|.
name|length
decl_stmt|;
name|int
name|n2
init|=
name|roundNumber
operator|%
name|ab
operator|.
name|length
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|ab
index|[
name|n1
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"-->"
argument_list|)
operator|.
name|append
argument_list|(
name|ab
index|[
name|n2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
return|return
name|roundNumber
return|;
block|}
comment|// extract properties to array, e.g. for "10.100.5" return int[]{10,100,5}.
DECL|method|propToIntArray
specifier|private
name|int
index|[]
name|propToIntArray
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|int
index|[]
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
block|}
return|;
block|}
name|ArrayList
name|a
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|s
argument_list|,
literal|":"
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|t
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|a
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|res
index|[]
init|=
operator|new
name|int
index|[
name|a
operator|.
name|size
argument_list|()
index|]
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Integer
operator|)
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|// extract properties to array, e.g. for "true.true.false" return booleab[]{true,false,false}.
DECL|method|propToBooleanArray
specifier|private
name|boolean
index|[]
name|propToBooleanArray
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|boolean
index|[]
block|{
name|Boolean
operator|.
name|valueOf
argument_list|(
name|s
argument_list|)
operator|.
name|booleanValue
argument_list|()
block|}
return|;
block|}
name|ArrayList
name|a
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|s
argument_list|,
literal|":"
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|t
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|a
operator|.
name|add
argument_list|(
operator|new
name|Boolean
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|res
index|[]
init|=
operator|new
name|boolean
index|[
name|a
operator|.
name|size
argument_list|()
index|]
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Boolean
operator|)
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * @return names of params set by round, for reports title    */
DECL|method|getColsNamesForValsByRound
specifier|public
name|String
name|getColsNamesForValsByRound
parameter_list|()
block|{
if|if
condition|(
name|colForValByRound
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|colForValByRound
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|colName
init|=
operator|(
name|String
operator|)
name|colForValByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|colName
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return values of params set by round, for reports lines.    */
DECL|method|getColsValuesForValsByRound
specifier|public
name|String
name|getColsValuesForValsByRound
parameter_list|(
name|int
name|roundNum
parameter_list|)
block|{
if|if
condition|(
name|colForValByRound
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|colForValByRound
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|colName
init|=
operator|(
name|String
operator|)
name|colForValByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|template
init|=
literal|" "
operator|+
name|colName
decl_stmt|;
if|if
condition|(
name|roundNum
operator|<
literal|0
condition|)
block|{
comment|// just append blanks
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|formatPaddLeft
argument_list|(
literal|"-"
argument_list|,
name|template
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// append actual values, for that round
name|Object
name|a
init|=
name|valByRound
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|instanceof
name|int
index|[]
condition|)
block|{
name|int
name|ai
index|[]
init|=
operator|(
name|int
index|[]
operator|)
name|a
decl_stmt|;
name|int
name|n
init|=
name|roundNum
operator|%
name|ai
operator|.
name|length
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
name|ai
index|[
name|n
index|]
argument_list|,
name|template
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|ab
index|[]
init|=
operator|(
name|boolean
index|[]
operator|)
name|a
decl_stmt|;
name|int
name|n
init|=
name|roundNum
operator|%
name|ab
operator|.
name|length
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|formatPaddLeft
argument_list|(
literal|""
operator|+
name|ab
index|[
name|n
index|]
argument_list|,
name|template
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return the round number.    */
DECL|method|getRoundNumber
specifier|public
name|int
name|getRoundNumber
parameter_list|()
block|{
return|return
name|roundNumber
return|;
block|}
comment|/**    * @return Returns the algorithmText.    */
DECL|method|getAlgorithmText
specifier|public
name|String
name|getAlgorithmText
parameter_list|()
block|{
return|return
name|algorithmText
return|;
block|}
block|}
end_class

end_unit

