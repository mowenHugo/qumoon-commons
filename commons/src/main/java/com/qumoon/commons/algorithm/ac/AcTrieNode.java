package com.qumoon.commons.algorithm.ac;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * trie树的节点封装。
 *
 * @author kevin
 */
public class AcTrieNode {

  private final char aChar;
  private final List<AcTrieNode> children = Lists.newArrayList();
  private final Set<String> outputWords = Sets.newHashSet();
  private AcTrieNode suffixNode;

  /**
   * 指定一个字母构造一个trie树节点。
   *
   * @param nodeChar 一个字母
   */
  public AcTrieNode(char nodeChar) {
    this.aChar = nodeChar;
  }

  /**
   * 得到节点的字母。
   *
   * @return 节点的字母
   */
  public char getChar() {
    return aChar;
  }

  /**
   * 得到失败转移的节点。
   *
   * @return 失败转移的节点
   */
  public AcTrieNode getSuffixNode() {
    return suffixNode;
  }

  public void setSuffixNode(AcTrieNode suffixNode) {
    this.suffixNode = suffixNode;
  }

  /**
   * 为当前节点增加一个孩子节点。
   *
   * @param child 孩子节点
   * @return 当前节点
   */
  public AcTrieNode addChild(AcTrieNode child) {
    children.add(child);
    return child;
  }

  /**
   * 得到当前节点可以输出的字符串。
   */
  public Set<String> getOutputWords() {
    return outputWords;
  }

  /**
   * 得到当前节点的孩子节点。
   */
  public Collection<AcTrieNode> getChildren() {
    return children;
  }

  /**
   * 判断是否是根节点。
   */
  public boolean isRoot() {
    return aChar == '#';

  }

  /**
   * 得到当前节点包含指定字母的节点
   *
   * @param nodeChar 指定字母
   * @return 若没有包含指定字母的节点返回null
   */
  public AcTrieNode getChild(char nodeChar) {
    for (AcTrieNode node : children) {
      if (node.getChar() == nodeChar) {
        return node;
      }
    }
    return null;
  }

  /**
   * 是否是输出节点。
   */
  public boolean isOutputNode() {
    return outputWords.size() > 0;
  }

  /**
   * 增加此节点的输出字符串。
   */
  public void addOutputWord(String word) {
    outputWords.add(word);
  }

  /**
   * 增加此节点的输出字符串。
   */
  public void addOutputWords(Set<String> words) {
    outputWords.addAll(words);
  }

  public String toString() {
    return "[" + String.valueOf(aChar) + ":" + outputWords + "]";
  }
}
