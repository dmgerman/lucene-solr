begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.tst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
package|;
end_package

begin_comment
comment|/**  * The class creates a TST node.  */
end_comment

begin_class
DECL|class|TernaryTreeNode
specifier|public
class|class
name|TernaryTreeNode
block|{
comment|/** the character stored by a node. */
DECL|field|splitchar
name|char
name|splitchar
decl_stmt|;
comment|/** a reference object to the node containing character smaller than this node's character. */
DECL|field|loKid
name|TernaryTreeNode
name|loKid
decl_stmt|;
comment|/**  	 *  a reference object to the node containing character next to this node's character as  	 *  occurring in the inserted token. 	 */
DECL|field|eqKid
name|TernaryTreeNode
name|eqKid
decl_stmt|;
comment|/** a reference object to the node containing character higher than this node's character. */
DECL|field|hiKid
name|TernaryTreeNode
name|hiKid
decl_stmt|;
comment|/**  	 * used by leaf nodes to store the complete tokens to be added to suggest list while  	 * auto-completing the prefix. 	 */
DECL|field|token
name|String
name|token
decl_stmt|;
DECL|field|val
name|Object
name|val
decl_stmt|;
block|}
end_class

end_unit

