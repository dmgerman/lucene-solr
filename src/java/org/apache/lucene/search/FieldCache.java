begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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

begin_comment
comment|/**  * Expert: Maintains caches of term values.  *  *<p>Created: May 19, 2004 11:13:14 AM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  */
end_comment

begin_interface
DECL|interface|FieldCache
specifier|public
interface|interface
name|FieldCache
block|{
comment|/** Indicator for StringIndex values in the cache. */
comment|// NOTE: the value assigned to this constant must not be
comment|// the same as any of those in SortField!!
DECL|field|STRING_INDEX
specifier|public
specifier|static
specifier|final
name|int
name|STRING_INDEX
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Expert: Stores term text values and document ordering data. */
DECL|class|StringIndex
specifier|public
specifier|static
class|class
name|StringIndex
block|{
comment|/** All the term values, in natural order. */
DECL|field|lookup
specifier|public
specifier|final
name|String
index|[]
name|lookup
decl_stmt|;
comment|/** For each document, an index into the lookup array. */
DECL|field|order
specifier|public
specifier|final
name|int
index|[]
name|order
decl_stmt|;
comment|/** Creates one of these objects */
DECL|method|StringIndex
specifier|public
name|StringIndex
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|String
index|[]
name|lookup
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
block|}
block|}
comment|/** Expert: The cache used internally by sorting and range query classes. */
DECL|field|DEFAULT
specifier|public
specifier|static
name|FieldCache
name|DEFAULT
init|=
operator|new
name|FieldCacheImpl
argument_list|()
decl_stmt|;
comment|/** Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as integers and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the integers.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if    * none is found, reads the terms in<code>field</code> as floats and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the floats.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found, reads the term values in<code>field</code> and returns an array    * of size<code>reader.maxDoc()</code> containing the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getStrings
specifier|public
name|String
index|[]
name|getStrings
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found reads the term values in<code>field</code> and returns    * an array of them in natural order, along with an array telling    * which element in the term array each document uses.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @return Array of terms and index into the array for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getStringIndex
specifier|public
name|StringIndex
name|getStringIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if    * none is found reads<code>field</code> to see if it contains integers, floats    * or strings, and then calls one of the other methods in this class to get the    * values.  For string values, a StringIndex is returned.  After    * calling this method, there is an entry in the cache for both    * type<code>AUTO</code> and the actual found type.    * @param reader  Used to get field values.    * @param field   Which field contains the values.    * @return int[], float[] or StringIndex.    * @throws IOException  If any error occurs.    */
DECL|method|getAuto
specifier|public
name|Object
name|getAuto
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found reads the terms out of<code>field</code> and calls the given SortComparator    * to get the sort values.  A hit in the cache will happen if<code>reader</code>,    *<code>field</code>, and<code>comparator</code> are the same (using<code>equals()</code>)    * as a previous call to this method.    * @param reader  Used to get field values.    * @param field   Which field contains the values.    * @param comparator Used to convert terms into something to sort by.    * @return Array of sort objects, one for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getCustom
specifier|public
name|Comparable
index|[]
name|getCustom
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|SortComparator
name|comparator
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

