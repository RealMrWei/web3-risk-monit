const {ethers } = require('hardhat');
const {expect} = require('chai');

describe("Vault test", function () { 
  let vault;
  let owner,user;
  // deploy contract before each test
  beforeEach(async function () {
    [owner,user] = await ethers.getSigners();
    const Vault = await ethers.getContractFactory("Vault");
    vault = await Vault.deploy();
  });

  // 1.test deploy sucess
  it("deploy sucess", async function () {
    expect(vault.target).to.not.be.undefined;
  });

  // 2.test deposit and emit 
  it("deposit", async function () {
    const amount = ethers.parseEther("1");
    await vault.deposit({value: amount});
    await expect(vault.connect(user).deposit({ value: amount }))
      .to.emit(vault, "Deposit")
      .withArgs(user.address, amount);
  });

  // 3.test withdraw and emit Withdraw
  it("withdraw", async function () {
    const amount = ethers.parseEther("1");
    await vault.connect(user).deposit({value: amount});
    await expect(vault.connect(user).withdraw(amount))
      .to.emit(vault, "Withdraw")
      .withArgs(user.address, amount);
  });


})