begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Holds a sequence of string components, specifying the hierarchical name of a  * category.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryPath
specifier|public
class|class
name|CategoryPath
implements|implements
name|Comparable
argument_list|<
name|CategoryPath
argument_list|>
block|{
comment|/** An empty {@link CategoryPath} */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|CategoryPath
name|EMPTY
init|=
operator|new
name|CategoryPath
argument_list|()
decl_stmt|;
comment|/**    * The components of this {@link CategoryPath}. Note that this array may be    * shared with other {@link CategoryPath} instances, e.g. as a result of    * {@link #subpath(int)}, therefore you should traverse the array up to    * {@link #length} for this path's components.    */
DECL|field|components
specifier|public
specifier|final
name|String
index|[]
name|components
decl_stmt|;
comment|/** The number of components of this {@link CategoryPath}. */
DECL|field|length
specifier|public
specifier|final
name|int
name|length
decl_stmt|;
comment|// Used by singleton EMPTY
DECL|method|CategoryPath
specifier|private
name|CategoryPath
parameter_list|()
block|{
name|components
operator|=
literal|null
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
block|}
comment|// Used by subpath
DECL|method|CategoryPath
specifier|private
name|CategoryPath
parameter_list|(
specifier|final
name|CategoryPath
name|copyFrom
parameter_list|,
specifier|final
name|int
name|prefixLen
parameter_list|)
block|{
comment|// while the code which calls this method is safe, at some point a test
comment|// tripped on AIOOBE in toString, but we failed to reproduce. adding the
comment|// assert as a safety check.
assert|assert
name|prefixLen
operator|>
literal|0
operator|&&
name|prefixLen
operator|<=
name|copyFrom
operator|.
name|components
operator|.
name|length
operator|:
literal|"prefixLen cannot be negative nor larger than the given components' length: prefixLen="
operator|+
name|prefixLen
operator|+
literal|" components.length="
operator|+
name|copyFrom
operator|.
name|components
operator|.
name|length
assert|;
name|this
operator|.
name|components
operator|=
name|copyFrom
operator|.
name|components
expr_stmt|;
name|length
operator|=
name|prefixLen
expr_stmt|;
block|}
comment|/** Construct from the given path components. */
DECL|method|CategoryPath
specifier|public
name|CategoryPath
parameter_list|(
specifier|final
name|String
modifier|...
name|components
parameter_list|)
block|{
assert|assert
name|components
operator|.
name|length
operator|>
literal|0
operator|:
literal|"use CategoryPath.EMPTY to create an empty path"
assert|;
for|for
control|(
name|String
name|comp
range|:
name|components
control|)
block|{
if|if
condition|(
name|comp
operator|==
literal|null
operator|||
name|comp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"empty or null components not allowed: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|components
operator|=
name|components
expr_stmt|;
name|length
operator|=
name|components
operator|.
name|length
expr_stmt|;
block|}
comment|/** Construct from a given path, separating path components with {@code delimiter}. */
DECL|method|CategoryPath
specifier|public
name|CategoryPath
parameter_list|(
specifier|final
name|String
name|pathString
parameter_list|,
specifier|final
name|char
name|delimiter
parameter_list|)
block|{
name|String
index|[]
name|comps
init|=
name|pathString
operator|.
name|split
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|comps
operator|.
name|length
operator|==
literal|1
operator|&&
name|comps
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|components
operator|=
literal|null
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|comp
range|:
name|comps
control|)
block|{
if|if
condition|(
name|comp
operator|==
literal|null
operator|||
name|comp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"empty or null components not allowed: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|comps
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|components
operator|=
name|comps
expr_stmt|;
name|length
operator|=
name|components
operator|.
name|length
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of characters needed to represent the path, including    * delimiter characters, for using with    * {@link #copyFullPath(char[], int, char)}.    */
DECL|method|fullPathLength
specifier|public
name|int
name|fullPathLength
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
return|return
literal|0
return|;
name|int
name|charsNeeded
init|=
literal|0
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|charsNeeded
operator|+=
name|components
index|[
name|i
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|charsNeeded
operator|+=
name|length
operator|-
literal|1
expr_stmt|;
comment|// num delimter chars
return|return
name|charsNeeded
return|;
block|}
comment|/**    * Compares this path with another {@link CategoryPath} for lexicographic    * order.    */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|CategoryPath
name|other
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|length
operator|<
name|other
operator|.
name|length
condition|?
name|length
else|:
name|other
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
operator|,
name|j
operator|++
control|)
block|{
name|int
name|cmp
init|=
name|components
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|components
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
return|return
operator|-
literal|1
return|;
comment|// this is 'before'
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
return|return
literal|1
return|;
comment|// this is 'after'
block|}
comment|// one is a prefix of the other
return|return
name|length
operator|-
name|other
operator|.
name|length
return|;
block|}
DECL|method|hasDelimiter
specifier|private
name|void
name|hasDelimiter
parameter_list|(
name|String
name|offender
parameter_list|,
name|char
name|delimiter
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"delimiter character '"
operator|+
name|delimiter
operator|+
literal|"' (U+"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|delimiter
argument_list|)
operator|+
literal|") appears in path component \""
operator|+
name|offender
operator|+
literal|"\""
argument_list|)
throw|;
block|}
DECL|method|noDelimiter
specifier|private
name|void
name|noDelimiter
parameter_list|(
name|char
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|char
name|delimiter
parameter_list|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|len
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|buf
index|[
name|offset
operator|+
name|idx
index|]
operator|==
name|delimiter
condition|)
block|{
name|hasDelimiter
argument_list|(
operator|new
name|String
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Copies the path components to the given {@code char[]}, starting at index    * {@code start}. {@code delimiter} is copied between the path components.    * Returns the number of chars copied.    *     *<p>    *<b>NOTE:</b> this method relies on the array being large enough to hold the    * components and separators - the amount of needed space can be calculated    * with {@link #fullPathLength()}.    */
DECL|method|copyFullPath
specifier|public
name|int
name|copyFullPath
parameter_list|(
name|char
index|[]
name|buf
parameter_list|,
name|int
name|start
parameter_list|,
name|char
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|idx
init|=
name|start
decl_stmt|;
name|int
name|upto
init|=
name|length
operator|-
literal|1
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
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|int
name|len
init|=
name|components
index|[
name|i
index|]
operator|.
name|length
argument_list|()
decl_stmt|;
name|components
index|[
name|i
index|]
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|buf
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|noDelimiter
argument_list|(
name|buf
argument_list|,
name|idx
argument_list|,
name|len
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
name|idx
operator|+=
name|len
expr_stmt|;
name|buf
index|[
name|idx
operator|++
index|]
operator|=
name|delimiter
expr_stmt|;
block|}
name|components
index|[
name|upto
index|]
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|components
index|[
name|upto
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|buf
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|noDelimiter
argument_list|(
name|buf
argument_list|,
name|idx
argument_list|,
name|components
index|[
name|upto
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
return|return
name|idx
operator|+
name|components
index|[
name|upto
index|]
operator|.
name|length
argument_list|()
operator|-
name|start
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|CategoryPath
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CategoryPath
name|other
init|=
operator|(
name|CategoryPath
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|length
operator|!=
name|other
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
comment|// not same length, cannot be equal
block|}
comment|// CategoryPaths are more likely to differ at the last components, so start
comment|// from last-first
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|!
name|components
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|other
operator|.
name|components
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|hash
init|=
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|components
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/** Calculate a 64-bit hash function for this path. */
DECL|method|longHashCode
specifier|public
name|long
name|longHashCode
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|hash
init|=
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|=
name|hash
operator|*
literal|65599
operator|+
name|components
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/** Returns a sub-path of this path up to {@code length} components. */
DECL|method|subpath
specifier|public
name|CategoryPath
name|subpath
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>=
name|this
operator|.
name|length
operator|||
name|length
operator|<
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
else|else
block|{
return|return
operator|new
name|CategoryPath
argument_list|(
name|this
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns a string representation of the path, separating components with    * '/'.    *     * @see #toString(char)    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|'/'
argument_list|)
return|;
block|}
comment|/**    * Returns a string representation of the path, separating components with the    * given delimiter.    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|char
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|components
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
name|delimiter
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|hasDelimiter
argument_list|(
name|components
index|[
name|i
index|]
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove last delimiter
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

