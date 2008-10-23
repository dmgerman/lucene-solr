begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|TermVectorParams
specifier|public
interface|interface
name|TermVectorParams
block|{
DECL|field|TV_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TV_PREFIX
init|=
literal|"tv."
decl_stmt|;
comment|/**   * Return Term Frequency info   * */
DECL|field|TF
specifier|public
specifier|static
specifier|final
name|String
name|TF
init|=
name|TV_PREFIX
operator|+
literal|"tf"
decl_stmt|;
comment|/**   * Return Term Vector position information   *   * */
DECL|field|POSITIONS
specifier|public
specifier|static
specifier|final
name|String
name|POSITIONS
init|=
name|TV_PREFIX
operator|+
literal|"positions"
decl_stmt|;
comment|/**   * Return offset information, if available   * */
DECL|field|OFFSETS
specifier|public
specifier|static
specifier|final
name|String
name|OFFSETS
init|=
name|TV_PREFIX
operator|+
literal|"offsets"
decl_stmt|;
comment|/**   * Return IDF information.  May be expensive   * */
DECL|field|IDF
specifier|public
specifier|static
specifier|final
name|String
name|IDF
init|=
name|TV_PREFIX
operator|+
literal|"idf"
decl_stmt|;
comment|/**    * Return TF-IDF calculation, i.e. (tf / idf).  May be expensive.    */
DECL|field|TF_IDF
specifier|public
specifier|static
specifier|final
name|String
name|TF_IDF
init|=
name|TV_PREFIX
operator|+
literal|"tf-idf"
decl_stmt|;
comment|/**    * Return all the options: TF, positions, offsets, idf    */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|String
name|ALL
init|=
name|TV_PREFIX
operator|+
literal|"all"
decl_stmt|;
comment|/**    * The fields to get term vectors for    */
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS
init|=
name|TV_PREFIX
operator|+
literal|"fl"
decl_stmt|;
comment|/**    * The Doc Ids (Lucene internal ids) of the docs to get the term vectors for    */
DECL|field|DOC_IDS
specifier|public
specifier|static
specifier|final
name|String
name|DOC_IDS
init|=
name|TV_PREFIX
operator|+
literal|"docIds"
decl_stmt|;
block|}
end_interface

end_unit

