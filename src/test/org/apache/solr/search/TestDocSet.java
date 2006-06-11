begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|OpenBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|BitSetIterator
import|;
end_import

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|TestDocSet
specifier|public
class|class
name|TestDocSet
extends|extends
name|TestCase
block|{
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|getRandomSet
specifier|public
name|OpenBitSet
name|getRandomSet
parameter_list|(
name|int
name|sz
parameter_list|,
name|int
name|bitsToSet
parameter_list|)
block|{
name|OpenBitSet
name|bs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|==
literal|0
condition|)
return|return
name|bs
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|bs
operator|.
name|fastSet
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|getHashDocSet
specifier|public
name|DocSet
name|getHashDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|bs
operator|.
name|cardinality
argument_list|()
index|]
decl_stmt|;
name|BitSetIterator
name|iter
init|=
operator|new
name|BitSetIterator
argument_list|(
name|bs
argument_list|)
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getBitDocSet
specifier|public
name|DocSet
name|getBitDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
return|return
operator|new
name|BitDocSet
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|getDocSet
specifier|public
name|DocSet
name|getDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
return|return
name|rand
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|==
literal|0
condition|?
name|getHashDocSet
argument_list|(
name|bs
argument_list|)
else|:
name|getBitDocSet
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|checkEqual
specifier|public
name|void
name|checkEqual
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|,
name|DocSet
name|set
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bs
operator|.
name|capacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|set
operator|.
name|exists
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSingle
specifier|protected
name|void
name|doSingle
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|int
name|sz
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|sz2
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|OpenBitSet
name|a1
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|OpenBitSet
name|a2
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz2
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DocSet
name|b1
init|=
name|getDocSet
argument_list|(
name|a1
argument_list|)
decl_stmt|;
name|DocSet
name|b2
init|=
name|getDocSet
argument_list|(
name|a2
argument_list|)
decl_stmt|;
comment|// System.out.println("b1="+b1+", b2="+b2);
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|a1
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|a2
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_and
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_or
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|a2
argument_list|)
expr_stmt|;
comment|// OpenBitSet a_xor = (OpenBitSet)a1.clone(); a_xor.xor(a2);
name|OpenBitSet
name|a_andn
init|=
operator|(
name|OpenBitSet
operator|)
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_and
argument_list|,
name|b1
operator|.
name|intersection
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_or
argument_list|,
name|b1
operator|.
name|union
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_andn
argument_list|,
name|b1
operator|.
name|andNot
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|intersectionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|unionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|andNotSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doMany
specifier|public
name|void
name|doMany
parameter_list|(
name|int
name|maxSz
parameter_list|,
name|int
name|iter
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|doSingle
argument_list|(
name|maxSz
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomDocSets
specifier|public
name|void
name|testRandomDocSets
parameter_list|()
block|{
name|doMany
argument_list|(
literal|300
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

