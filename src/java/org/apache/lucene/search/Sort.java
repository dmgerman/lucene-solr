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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Encapsulates sort criteria for returned hits.  *  *<p>The fields used to determine sort order must be carefully chosen.  * Documents must contain a single term in such a field,  * and the value of the term should indicate the document's relative position in  * a given sort order.  The field must be indexed, but should not be tokenized,  * and does not need to be stored (unless you happen to want it back with the  * rest of your document data).  In other words:  *  *<dl><dd><code>document.add (new Field ("byNumber", Integer.toString(x), false, true, false));</code>  *</dd></dl>  *  *<p><h3>Valid Types of Values</h3>  *  *<p>There are three possible kinds of term values which may be put into  * sorting fields: Integers, Floats, or Strings.  Unless  * {@link SortField SortField} objects are specified, the type of value  * in the field is determined by using a regular expression against the  * first term in the field.  *  *<p>Integer term values should contain only digits and an optional  * preceeding negative sign.  Values must be base 10 and in the range  *<code>Integer.MIN_VALUE</code> and<code>Integer.MAX_VALUE</code> inclusive.  * Documents which should appear first in the sort  * should have low value integers, later documents high values  * (i.e. the documents should be numbered<code>1..n</code> where  *<code>1</code> is the first and<code>n</code> the last).  *  *<p>Float term values should conform to values accepted by  * {@link Float Float.valueOf(String)} (except that<code>NaN</code>  * and<code>Infinity</code> are not supported).  * Documents which should appear first in the sort  * should have low values, later documents high values.  *  *<p>String term values can contain any valid String, but should  * not be tokenized.  The values are sorted according to their  * {@link Comparable natural order}.  Note that using this type  * of term value has higher memory requirements than the other  * two types - see {@link FieldSortedHitQueue FieldSortedHitQueue}.  *  *<p><h3>Object Reuse</h3>  *  *<p>One of these objects can be  * used multiple times and the sort order changed between usages.  *  *<p>This class is thread safe.  *  *<p><h3>Memory Usage</h3>  *  * See {@link FieldSortedHitQueue FieldSortedHitQueue} for  * information on the memory requirements of sorting hits.  *  *<p>Created: Feb 12, 2004 10:53:57 AM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  */
end_comment

begin_class
DECL|class|Sort
specifier|public
class|class
name|Sort
implements|implements
name|Serializable
block|{
comment|/** Represents sorting by computed relevance. Using this sort criteria 	 * returns the same results with slightly more overhead as calling 	 * Searcher#search() without a sort criteria. */
DECL|field|RELEVANCE
specifier|public
specifier|static
specifier|final
name|Sort
name|RELEVANCE
init|=
operator|new
name|Sort
argument_list|()
decl_stmt|;
comment|/** Represents sorting by index order. */
DECL|field|INDEXORDER
specifier|public
specifier|static
specifier|final
name|Sort
name|INDEXORDER
init|=
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
decl_stmt|;
comment|// internal representation of the sort criteria
DECL|field|fields
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** Sorts by computed relevance.  This is the same sort criteria as 	 * calling Searcher#search() without a sort criteria, only with 	 * slightly more overhead. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|SortField
index|[]
block|{
name|SortField
operator|.
name|FIELD_SCORE
block|,
name|SortField
operator|.
name|FIELD_DOC
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts by the terms in<code>field</code> then by index order (document 	 * number). */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|setSort
argument_list|(
name|field
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts possibly in reverse by the terms in<code>field</code> then by 	 * index order (document number). */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|setSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts in succession by the terms in each field. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|String
index|[]
name|fields
parameter_list|)
block|{
name|setSort
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts by the criteria in the given SortField. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|SortField
name|field
parameter_list|)
block|{
name|setSort
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts in succession by the criteria in each SortField. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
name|setSort
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the sort to the terms in<code>field</code> then by index order 	 * (document number). */
DECL|method|setSort
specifier|public
specifier|final
name|void
name|setSort
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|setSort
argument_list|(
name|field
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the sort to the terms in<code>field</code> possibly in reverse, 	 * then by index order (document number). */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|SortField
index|[]
name|nfields
init|=
operator|new
name|SortField
index|[]
block|{
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|AUTO
argument_list|,
name|reverse
argument_list|)
block|,
name|SortField
operator|.
name|FIELD_DOC
block|}
decl_stmt|;
name|fields
operator|=
name|nfields
expr_stmt|;
block|}
comment|/** Sets the sort to the terms in each field in succession. */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|String
index|[]
name|fieldnames
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|fieldnames
operator|.
name|length
decl_stmt|;
name|SortField
index|[]
name|nfields
init|=
operator|new
name|SortField
index|[
name|n
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|nfields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldnames
index|[
name|i
index|]
argument_list|,
name|SortField
operator|.
name|AUTO
argument_list|)
expr_stmt|;
block|}
name|fields
operator|=
name|nfields
expr_stmt|;
block|}
comment|/** Sets the sort to the given criteria. */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|SortField
name|field
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[]
block|{
name|field
block|}
expr_stmt|;
block|}
comment|/** Sets the sort to the given criteria in succession. */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
block|}
end_class

end_unit

