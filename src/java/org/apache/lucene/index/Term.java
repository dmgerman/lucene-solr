begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**   A Term represents a word from text.  This is the unit of search.  It is   composed of two elements, the text of the word, as a string, and the name of   the field that the text occured in, an interned string.    Note that terms may represent more than words from text fields, but also   things like dates, email addresses, urls, etc.  */
end_comment

begin_class
DECL|class|Term
specifier|public
specifier|final
class|class
name|Term
implements|implements
name|Comparable
implements|,
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|text
name|String
name|text
decl_stmt|;
comment|/** Constructs a Term with the given field and text.    *<p>Note that a null field or null text value results in undefined    * behavior for most Lucene APIs that accept a Term parameter. */
DECL|method|Term
specifier|public
name|Term
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|)
block|{
name|this
argument_list|(
name|fld
argument_list|,
name|txt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a Term with the given field and empty text.    * This serves two purposes: 1) reuse of a Term with the same field.    * 2) pattern for a query.    *     * @param fld    */
DECL|method|Term
specifier|public
name|Term
parameter_list|(
name|String
name|fld
parameter_list|)
block|{
name|this
argument_list|(
name|fld
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Term
name|Term
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|,
name|boolean
name|intern
parameter_list|)
block|{
name|field
operator|=
name|intern
condition|?
name|fld
operator|.
name|intern
argument_list|()
else|:
name|fld
expr_stmt|;
comment|// field names are interned
name|text
operator|=
name|txt
expr_stmt|;
comment|// unless already known to be
block|}
comment|/** Returns the field of this term, an interned string.   The field indicates     the part of a document which this term came from. */
DECL|method|field
specifier|public
specifier|final
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Returns the text of this term.  In the case of words, this is simply the     text of the word.  In the case of dates and other types, this is an     encoding of the object as a string.  */
DECL|method|text
specifier|public
specifier|final
name|String
name|text
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/**    * Optimized construction of new Terms by reusing same field as this Term    * - avoids field.intern() overhead     * @param text The text of the new term (field is implicitly same as this Term instance)    * @return A new Term    */
DECL|method|createTerm
specifier|public
name|Term
name|createTerm
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Compares two terms, returning true iff they have the same       field and text. */
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Term
operator|)
condition|)
return|return
literal|false
return|;
name|Term
name|other
init|=
operator|(
name|Term
operator|)
name|o
decl_stmt|;
return|return
name|field
operator|==
name|other
operator|.
name|field
operator|&&
name|text
operator|.
name|equals
argument_list|(
name|other
operator|.
name|text
argument_list|)
return|;
block|}
comment|/** Combines the hashCode() of the field and the text. */
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|+
name|text
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|Term
operator|)
name|other
argument_list|)
return|;
block|}
comment|/** Compares two terms, returning a negative integer if this     term belongs before the argument, zero if this term is equal to the     argument, and a positive integer if this term belongs after the argument.      The ordering of terms is first by field, then by text.*/
DECL|method|compareTo
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|Term
name|other
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
name|other
operator|.
name|field
condition|)
comment|// fields are interned
return|return
name|text
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|text
argument_list|)
return|;
else|else
return|return
name|field
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
comment|/** Resets the field and text of a Term. */
DECL|method|set
specifier|final
name|void
name|set
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|)
block|{
name|field
operator|=
name|fld
expr_stmt|;
name|text
operator|=
name|txt
expr_stmt|;
block|}
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|field
operator|+
literal|":"
operator|+
name|text
return|;
block|}
DECL|method|readObject
specifier|private
name|void
name|readObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|in
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

